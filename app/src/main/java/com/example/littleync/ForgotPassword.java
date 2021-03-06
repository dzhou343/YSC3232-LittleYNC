package com.example.littleync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.littleync.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Loads the forgot password page, to allow users to reset their password using their known email address.
 */
public class ForgotPassword extends AppCompatActivity {

    private EditText forgotPasswordInput;
    private String emailToReset;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        forgotPasswordInput = findViewById(R.id.forgot_password_input);
        resetButton = findViewById(R.id.submit_forgot_password);
    }

    /**
     * Called when reset password button is called, to send an email to the user's email address where they can type in a new password.
     *
     * @param view
     * @return void
     */
    public void resetPassword(View view) {
        try {
            resetButton.setEnabled(false);
            emailToReset = forgotPasswordInput.getText().toString();
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailToReset).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Forgot Email output", "Successfully reset password");
                                Toast.makeText(getApplicationContext(), "Success. Please check your email", Toast.LENGTH_LONG).show();
                                resetButton.setEnabled(true);
                                ForgotPassword.super.finish();
                            } else {
                                forgotPasswordInput.setError(task.getException().getMessage());
                                resetButton.clearFocus();
                                resetButton.setEnabled(true);
                            }
                        }
                    }
            );
        } catch (Exception e) {
            forgotPasswordInput.setError(e.getMessage());
            resetButton.clearFocus();
            resetButton.setEnabled(true);
        }

    }
}


