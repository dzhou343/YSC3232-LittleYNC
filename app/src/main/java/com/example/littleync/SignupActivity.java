package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.controller.Login;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Import the EditText and button type inside of the signup_page
 */

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.view.View;

import java.util.ArrayList;


public class SignupActivity extends AppCompatActivity {

    private Login loginObject = new Login();
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
                        User usr = new User(userNameBox.getText().toString(), 1,1,1,1,0,0,0,new ArrayList<String>(),0);
                        userDoc = fs.collection("users").document(FirebaseAuth.getInstance().getUid().toString());
                        usr.writeToDatabase(userDoc);
                        //submitSignUp.setText((task.getResult().toString()));
                        SignupActivity.super.finish();
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