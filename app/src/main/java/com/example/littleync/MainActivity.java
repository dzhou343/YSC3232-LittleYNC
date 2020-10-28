package com.example.littleync;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.example.littleync.controller.Login;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.example.littleync.MarketplaceActivity;

public class MainActivity extends AppCompatActivity {

    private EditText emailLogin;
    private EditText passwordLogin;
    public static String UID = null;
    private Boolean _b = true;
    static User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * set the logout
         */
        Login log = new Login();
        log.getMyAuthInstance().signOut();
        UID = null;

        /**
         * Check if the user is already logged in
         */
        if (log.getMyAuthInstance().getCurrentUser() != null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.travel_page);
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_page);

            emailLogin = findViewById(R.id.input_email);
            passwordLogin = findViewById(R.id.input_password);

            /**TODO: figure out the UI
             *
              */
            /*emailLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        emailLogin.setHint("");
                        emailLogin.setVisibility(View.VISIBLE);

                        try {
                            System.out.println(emailLogin.getText().toString());
                            emailLogin.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    emailLogin.setVisibility(View.VISIBLE);
                                    //passwordLogin.setVisibility(View.VISIBLE);
                                }


                                @Override
                                public void afterTextChanged(Editable s) {
                                    s.toString();
                                    if (_b) {
                                        emailLogin.setVisibility(View.VISIBLE);
                                        //emailLogin.setHint(emailLogin.getText().toString());
                                        _b = false;
                                    } else {

                                    }

                                    //passwordLogin.setVisibility(View.VISIBLE);


                                }
                            });
                        } catch (Exception e) {
                            System.out.println("OnCreateException");
                        }

                    } else if (!hasFocus) {
                    }

                }
            });*/



        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.print("onstart");

    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.print("onResume");

    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.print("onPause");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.print("onRestart");

    }

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.LOGIN";

    /**
     * Called when the user taps the Login button
     *
     * @param view
     *
     */
    public void loginButton(View view) {
        /**
         * Creates a new Login() object, to check whether user has already been authenticated.
         */
        final Login log = new Login();

        /**
         * user variable here holds the current user object.
         */


        try {

            //final String user = log.getMyAuthInstance().getUid().toString();

            //if (!emailLogin.equals(null) && !passwordLogin.equals(null)) {
            log.getMyAuthInstance().signInWithEmailAndPassword(emailLogin.getText().toString(), passwordLogin.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        /**
                         * Creates a user object
                         */
                        final String user = log.getMyAuthInstance().getUid().toString();
                        System.out.println();
                        Log.d("Login results:","successfully signed in!");
                        Log.d("UID is",String.format(user));
                        UID = user;
                        Intent intent = new Intent(MainActivity.this, TravelActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d("Login results:","nope! Didn't sign in!");
                        Log.d("Exception",task.getException().toString());
                        emailLogin.setError(task.getException().toString());
                        passwordLogin.setError(task.getException().toString());
                    }

                }
            });

        //}

        } catch (Exception e) {
            System.out.println(e);
            emailLogin.setError(e.toString());
            passwordLogin.setError(e.toString());

        }
        /**
         * Move to new Travel Activity page
         */

    }

    public void clearText(View view) {
    }

    // Called when the user presses the sign-up text.
    public void signUp(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void goToRandomPage(View view) {
        Intent intent = new Intent(this, RandomPage.class);
        startActivity(intent);
    }

}