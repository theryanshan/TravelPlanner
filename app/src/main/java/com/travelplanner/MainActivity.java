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

    private void writePoi(String rank, String name, String address, String latitude, String longitude, String image) {
        Poi poi = new Poi(name, address, latitude, longitude, image);
        database.child(rank).setValue(poi);
    }

    private void InitiatePoiDB(){
        database = FirebaseDatabase.getInstance().getReference().child("POI");
        writePoi("1","Golden Gate Bridge","Golden Gate Bridge, San Francisco, CA","37.8199286", "-122.4804438","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-golden-gate-bridge.jpg?alt=media&token=6d652717-b529-4cb2-9966-223fbb6fbb99");
        writePoi("2","Alcatraz Island","San Francisco, CA 94133", "37.8269775", "-122.4251442","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-alcatraz-island.jpg?alt=media&token=6ca7facc-4edd-425e-a0d8-ec024b083a2f");
        writePoi("3","Fisherman's Wharf","The Embarcadero, San Francisco, CA 94133","37.80811", "-122.4253858", "https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-fishermans-wharf.jpg?alt=media&token=890bddcc-e5f9-4b6a-8ace-c92d255d4e10");
        writePoi("4","Powell/Mason Cable Car","2350 Taylor St, San Francisco, CA 94133","37.8043405","-122.4170709","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-cable-cars.jpg?alt=media&token=4fc0358c-61d6-44b3-880c-559419634e12");
        writePoi("5","Golden Gate Park","Golden Gate Park, San Francisco, CA 94122","37.8043353","-122.4499018","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-golden-gate-park.jpg?alt=media&token=75db925f-da75-4801-9e1e-855be62672c8");
        writePoi("6","Chinatown","Stockton St Tunnel, San Francisco, CA 94108","37.7915257","-122.4096154","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-chinatown.jpg?alt=media&token=4137a214-a36b-4558-8217-1722c58fc91a");
        writePoi("7","Legion of Honor","100 34th Ave, San Francisco, CA 94121","37.7844779","-122.5030793","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-legion-of-honor-and-fountain.jpg?alt=media&token=ba97ce90-9a4e-4071-8f8c-e7dc0a3f78e2");
        writePoi("8","Palace of Fine Arts","3601 Lyon St, San Francisco, CA 94123","37.8020068","-122.4507942","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-palace-of-fine-arts.jpg?alt=media&token=b57756d4-a45a-481a-9f40-f3c191179b1d");
        writePoi("9","California Academy of Sciences","55 Music Concourse Dr, San Francisco, CA 94118","37.7698646","-122.4682834","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-attractions-academy-of-sciences-green-roof.jpg?alt=media&token=054773d3-948c-4b85-87cd-8111a872e61f");
        writePoi("10","San Francisco Museum of Modern Art","151 3rd St, San Francisco, CA 94103","37.7859072","-122.402989","https://firebasestorage.googleapis.com/v0/b/travelplanner-ffc94.appspot.com/o/california-san-francisco-museum-of-modern-art.jpg?alt=media&token=f341b07e-e68d-43ce-8ee0-b2a5a2853448");
    }
}
