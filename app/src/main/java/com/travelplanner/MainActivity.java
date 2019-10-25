package com.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    protected TextInputLayout emailTextInputLayout ;
    protected TextInputLayout passwordTextInputLayout ;
    protected DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitiatePoiDB();
        if (isServiceOK()) {
            init();
        }
    }

    private void init() {
        database = FirebaseDatabase.getInstance().getReference().child("User");
        emailTextInputLayout = findViewById(R.id.log_in_email);
        passwordTextInputLayout = findViewById(R.id.log_in_password);
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Utils.SHA256Encryption(emailTextInputLayout.getEditText().getText().toString());
                final String password = Utils.md5Encryption(passwordTextInputLayout.getEditText().getText().toString());
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(email)) {
                            Toast.makeText(getBaseContext(),"The email doesn't exist.", Toast.LENGTH_SHORT).show();
                        } else if (password.equals(dataSnapshot.child(email).child("user_password").getValue())) {
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        } else {
                            Toast.makeText(getBaseContext(),"The password is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Button btnSignUp = findViewById(R.id.btn_signUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServiceOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void writePoi(String rank, String name, String address, String image) {
        Poi poi = new Poi(name, address, image);
        database.child(rank).setValue(poi);
    }

    private void InitiatePoiDB(){
        database = FirebaseDatabase.getInstance().getReference().child("POI");
        writePoi("1","Golden Gate Bridge","Golden Gate Bridge, San Francisco, CA","gs://travelplanner-ffc94.appspot.com/california-san-francisco-golden-gate-bridge.jpg");
        writePoi("2","Alcatraz Island","San Francisco, CA 94133","gs://travelplanner-ffc94.appspot.com/california-san-francisco-alcatraz-island.jpg");
        writePoi("3","Fisherman's Wharf","The Embarcadero, San Francisco, CA 94133","gs://travelplanner-ffc94.appspot.com/california-san-francisco-fishermans-wharf.jpg");
        writePoi("4","Powell/Mason Cable Car","2350 Taylor St, San Francisco, CA 94133","gs://travelplanner-ffc94.appspot.com/california-san-francisco-cable-cars.jpg");
        writePoi("5","Golden Gate Park","Golden Gate Park, San Francisco, CA 94122","gs://travelplanner-ffc94.appspot.com/california-san-francisco-golden-gate-park.jpg");
        writePoi("6","Chinatown","Stockton St Tunnel, San Francisco, CA 94108","gs://travelplanner-ffc94.appspot.com/california-san-francisco-chinatown.jpg");
        writePoi("7","Legion of Honor","100 34th Ave, San Francisco, CA 94121","gs://travelplanner-ffc94.appspot.com/california-san-francisco-legion-of-honor-and-fountain.jpg");
        writePoi("8","Palace of Fine Arts","3601 Lyon St, San Francisco, CA 94123","gs://travelplanner-ffc94.appspot.com/california-san-francisco-palace-of-fine-arts.jpg");
        writePoi("9","California Academy of Sciences","55 Music Concourse Dr, San Francisco, CA 94118","gs://travelplanner-ffc94.appspot.com/california-san-francisco-attractions-academy-of-sciences-green-roof.jpg");
        writePoi("10","San Francisco Museum of Modern Art","151 3rd St, San Francisco, CA 94103","gs://travelplanner-ffc94.appspot.com/california-san-francisco-museum-of-modern-art.jpg");
    }
}
