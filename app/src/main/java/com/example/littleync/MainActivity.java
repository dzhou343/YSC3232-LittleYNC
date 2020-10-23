package com.example.littleync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.littleync.controller.Login;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
    }

    /**
     * Called when the user taps the Login button
     */
    public void loginButton(View view) {
        /**
         * Creates a new Login() object, to check whether user has already been authenticated.
         */
        Login log = new Login();

        /**
         * user variable here holds the current user object.
         */
        FirebaseUser user = log.getMyAuthInstance().getCurrentUser();
        if (user == null) {
            //System.out.println("User Not logged in");
            Intent intent = new Intent(this, TravelActivity.class);
            startActivity(intent);
        }

        /**
         * Debug code
         */
        else {
            System.out.println(user.getDisplayName());
            System.out.println(user.getUid());
            Intent intent = new Intent(this, TravelActivity.class);
            startActivity(intent);

        }


        /**
         * Move to new Travel Activity page
         */

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