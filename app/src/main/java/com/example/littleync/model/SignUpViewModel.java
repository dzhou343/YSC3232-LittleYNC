package com.example.littleync.model;

/**
 * Import the EditText type inside of the signup_page
 */

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;

/**
 * import AppComptActivity for findViewById, to edit the value of email, passwords
 */

import androidx.appcompat.app.AppCompatActivity;
/**
 * Import the resources folder
 */
import com.example.littleync.R;
import com.example.littleync.SignupActivity;

public class SignUpViewModel {
/*
    *//**
     * Instantiate from layout's view id
     *//*
    private String username;
    final private EditText email = findViewById(R.id.signUpEmail);
    private EditText password = findViewById(R.id.signUpPassword);
    private EditText password2 = findViewById(R.id.signUpPassword2);
    private Button submitSingUp = findViewById(R.id.button7);

    public void signUpSubmit(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        System.out.println(email.getText());
        System.out.println(password.getText());
        System.out.println(password2.getText());
    }

    public String getUsername() {
        return username;
    }

    public EditText getEmail() {
        return email;
    }

    public EditText getPassword() {
        return password;
    }

    public EditText getPassword2() {
        return password2;
    }

    public Button getSubmitSingUp() {
        return submitSingUp;
    }


    */
}
