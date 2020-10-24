package com.example.littleync.controller;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

/**
 * Title:
 * <p>
 * <p>
 * Description about the class
 *
 * @author Yun Da
 * <p>
 * getMyAuthInstance()
 * @return myAuthInstance
 * @return authID
 * Instantiate myAuth object with a log in object.
 * @see LoginActivity
 */
public class Login {
    static FirebaseAuth myAuthInstance = FirebaseAuth.getInstance();
    static String authID = FirebaseAuth.getInstance().getUid();

    /**
     * @param void
     * @return FirebaseAuth
     */
    public static FirebaseAuth getMyAuthInstance() {
        return myAuthInstance;
    }

    public static String getAuthID() {
        return authID;
    }

    public void loginValidate(String _email, String _password) {
    }
}
