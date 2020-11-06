package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.controller.UserNameAlreadyUsed;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Import the EditText and button type inside of the signup_page
 */

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.littleync.LoginActivity.userInstance;


public class SignupActivity extends AppCompatActivity {
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

    /**createUser() sends a request to firebase only when we have ensured that the passwords match and that the username is unique.
     * It does not take in any parameters as the email, username, and password information are all taken from SignUpActivity class.
     *
     */
    public void createUser(){
        userInstance.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                    //Generate the userName for the person
                    UserProfileChangeRequest updateForProfile = new UserProfileChangeRequest.Builder().setDisplayName(userNameBox.getText().toString()).build();
                    userInstance.getCurrentUser().updateProfile(updateForProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("USER PROFILE", "UPDATED SUCCESS");
                            } else {
                                Log.d("USER PROFILE", "UPDATED FAILED");
                            }
                        }
                    });

                    //submitSignUp.setText((task.getResult().toString()));
                    submitSignUp.setEnabled(true);
                    userInstance.signOut();
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
    }

    /**
     * signUpSubmit is the button which is linked to the signup button on the signup page.
     * @param view
     */
    public void signUpSubmit(View view) {
        submitSignUp.setEnabled(false);
        try {
            if (password.getText().toString().equals(password2.getText().toString())) {
                Query queryExistingUserName = fs.collection("users")
                        .whereEqualTo("userName", userNameBox.getText().toString());
                synchronized (this) {
                    queryExistingUserName
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            try {
                                                for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                                                    Log.d("SIGNUPACTIVITY",ds.getData().toString());
                                                }

                                                throw new UserNameAlreadyUsed();
                                            } catch (UserNameAlreadyUsed u) {
                                                Toast.makeText(getApplicationContext(), String.format("ERROR: %s", u.getMessage()), Toast.LENGTH_LONG).show();
                                                userNameBox.setError(u.getMessage());
                                                email.setError(u.getMessage());
                                                password.setError(u.getMessage());
                                                password2.setError(u.getMessage());
                                                Log.d("ERROR", u.getMessage());
                                                submitSignUp.setEnabled(true);
                                            }
                                        } else {
                                            System.out.println("DS IS NULL");
                                            createUser();
                                        }
                                    }
                                }
                            });



                }

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

    public void goToTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

}