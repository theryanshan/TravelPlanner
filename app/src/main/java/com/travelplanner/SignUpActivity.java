package com.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    protected TextInputLayout emailTextInputLayout ;
    protected TextInputLayout passwordTextInputLayout ;
    protected TextInputLayout firstNameTextInputLayout ;
    protected TextInputLayout lastNameTextInputLayout ;
    protected Button btnSignUp;
    protected DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //init();
        Log.d(TAG,"init: initializing");

        database = FirebaseDatabase.getInstance().getReference().child("User");
        emailTextInputLayout = findViewById(R.id.sign_up_email);
        passwordTextInputLayout = findViewById(R.id.sign_up_password);
        firstNameTextInputLayout = findViewById(R.id.sign_up_first_name);
        lastNameTextInputLayout = findViewById(R.id.sign_up_last_name);
        btnSignUp = findViewById(R.id.btn_signUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Utils.SHA256Encryption(emailTextInputLayout.getEditText().getText().toString());
                final String password = Utils.md5Encryption(passwordTextInputLayout.getEditText().getText().toString());
                final String firstName = firstNameTextInputLayout.getEditText().getText().toString();
                final String lastName = lastNameTextInputLayout.getEditText().getText().toString();

                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(email)) {
                            Toast.makeText(getBaseContext(), "The email is already signed up, please try again",
                                    Toast.LENGTH_LONG).show();
                            emailTextInputLayout.getEditText().getText().clear();
                        } else if (!email.isEmpty() && !password.isEmpty()) {
                            final User user = new User();
                            user.setUser_email(email);
                            user.setUser_password(password);
                            user.setUser_firstName(firstName);
                            user.setUser_lastName(lastName);
                            database.child(user.getUser_email()).setValue(user);
                            Toast.makeText(getBaseContext(), "user has successfully signed up",
                                    Toast.LENGTH_LONG).show();
                            goToHomePage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //private void init() {
    //Log.d(TAG,"init: initializing");
    // Backend can continue filling this part out
    //}
    private void goToHomePage() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
