package com.example.littleync.controller;


import com.google.firebase.auth.FirebaseAuth;

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
}
