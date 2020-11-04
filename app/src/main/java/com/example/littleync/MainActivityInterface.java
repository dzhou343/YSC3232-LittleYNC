package com.example.littleync;

import android.location.Location;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.TreeMap;

public interface MainActivityInterface {


    void loginButton(View view);

    void forgotPasswordScreen(View view);

    // Called when the user presses the sign-up text.
    void signUp(View view);

}
