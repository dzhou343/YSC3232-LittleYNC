package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.littleync.controller.Login;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Import the EditText and button type inside of the signup_page
 */

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class SignupActivity extends AppCompatActivity implements SignupActivityInterface {

    private FirebaseAuth loginObject = FirebaseAuth.getInstance();
    private EditText email;
    private EditText password;
    private EditText password2;
    private EditText userNameBox;
    private Button submitSignUp;
    private DocumentReference userDoc;
    private final static String TAG = "signUp";
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        /**
         * Instantiate from layout's view id
         */
        final String username;
        final EditText email = findViewById(R.id.sign_up_email);
        final EditText password = findViewById(R.id.sign_up_password);
        final EditText password2 = findViewById(R.id.sign_up_password2);
        final Button submitSignUp = findViewById(R.id.submitSignUpButton);
        final EditText userName = findViewById(R.id.username);
        this.email = email;
        this.password = password;
        this.password2 = password2;
        this.submitSignUp = submitSignUp;
        this.userNameBox = userName;
    }

    @Override
    public void signUpSubmit(View view) {
        submitSignUp.setEnabled(false);
        try {
            if (password.getText().toString().equals(password2.getText().toString())) {
                loginObject.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), String.format("Sign up successful for user %s", userNameBox.getText().toString()), Toast.LENGTH_LONG).show();
                            Log.d(TAG, String.format("Sign up successful for user %s", userNameBox.getText().toString()));
                            email.setError(null);
                            password.setError(null);
                            password2.setError(null);
                            User user = new User(userNameBox.getText().toString(), 1, 1, 1, 1, 0, 0, 0, new ArrayList<String>(), 0);
                            userDoc = fs.collection("users").document(FirebaseAuth.getInstance().getUid().toString());
                            user.writeToDatabaseDirectly(userDoc);
                            UserProfileChangeRequest updateForProfile = new UserProfileChangeRequest.Builder().setDisplayName(userNameBox.getText().toString()).build();
                            loginObject.getCurrentUser().updateProfile(updateForProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d("USER PROFILE","UPDATED SUCCESS");
                                    }
                                    else{
                                        Log.d("USER PROFILE","UPDATED FAILED");
                                    }
                                }
                            });

                            //submitSignUp.setText((task.getResult().toString()));
                            submitSignUp.setEnabled(true);
                            SignupActivity.super.finish();

                        } else if (!task.isSuccessful()) {
                            Log.d(TAG, "login failed");
                            Log.d(TAG, task.toString());
                            Log.d(TAG, task.getException().getMessage());
                            email.setError(task.getException().getMessage());
                            password.setError(task.getException().getMessage());
                            password2.setError(task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), String.format("Sign up failure for user %s: %s", userNameBox.getText().toString(), task.getException().getMessage()), Toast.LENGTH_LONG).show();
                            submitSignUp.setEnabled(true);
                        }
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), String.format("Sign up failure for user %s: %s", userNameBox.getText().toString(), "passwords don't match!"), Toast.LENGTH_LONG).show();
                email.setError("passwords don't match!");
                password.setError("passwords don't match!");
                password2.setError("passwords don't match!");
                Log.d("ERROR", "passwords don't match!");
                submitSignUp.setEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), String.format("ERROR: %s", e.getMessage()), Toast.LENGTH_LONG).show();
            userNameBox.setError(e.getMessage());
            email.setError(e.getMessage());
            password.setError(e.getMessage());
            password2.setError(e.getMessage());
            Log.d("ERROR", e.getMessage());
            submitSignUp.setEnabled(true);
        }

    }


}