package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.littleync.controller.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Import the EditText and button type inside of the signup_page
 */

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.view.View;


public class SignupActivity extends AppCompatActivity {

    private Login loginObject = new Login();
    private EditText email;
    private EditText password;
    private EditText password2;
    private Button submitSignUp;
    private final static String TAG = "signUp";

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
        this.email = email;
        this.password = password;
        this.password2 = password2;
        this.submitSignUp = submitSignUp;
    }

    public void signUpSubmit(View view) {

        System.out.println(email.getText().toString());
        System.out.println(password.getText().toString());
        System.out.println(password2.getText().toString());
        if (password.getText().toString().equals(password2.getText().toString())) {
            loginObject.getMyAuthInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, String.format("Sign up successful for user %s", email));
                        System.out.println(String.format("Sign up successful for %s", email));
                        email.setError(null);
                        password.setError(null);
                        password2.setError(null);
                        submitSignUp.setText((task.getResult().toString()));
                    } else if (!task.isSuccessful()) {
                        Log.d(TAG, "login failed");
                        Log.d(TAG, task.toString());
                        Log.d(TAG, task.getException().getMessage());
                        email.setError(task.getException().getMessage());
                        password.setError(task.getException().getMessage());
                        password2.setError(task.getException().getMessage());
                    }
                }
            });

        }
    }

    {
        System.out.println("passwords don't match!");
    }

}