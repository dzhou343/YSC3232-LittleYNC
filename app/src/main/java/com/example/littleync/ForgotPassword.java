package com.example.littleync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.littleync.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    private EditText forgotPasswordInput;
    private String emailToReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        forgotPasswordInput = findViewById(R.id.forgot_password_input);
    }

    public void resetPassword(View view) {
        emailToReset = forgotPasswordInput.getText().toString();
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailToReset).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Forgot Email output", "Successfully reset password");
                            Toast.makeText(getApplicationContext(), "Success. Please check your email", Toast.LENGTH_LONG).show();
                            ForgotPassword.super.finish();
                        } else {
                            forgotPasswordInput.setError(task.getResult().toString());
                        }
                    }
                }
        );

    }
}


