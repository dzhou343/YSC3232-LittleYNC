package com.example.littleync.model;

///The following packages below are in preparation for reading and writing
import com.google.firebase.*;
import com.google.firebase.firestore.*;
import com.google.firebase.Timestamp;
import com.google.android.gms.tasks.*;


public class OnlineDatabase {
    String userID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ///Function to read user data
    public DocumentReference userRead(String userIdInput){
        this.userID = userIdInput;
        System.out.println(userIdInput + userID);
        DocumentReference dRef = db.collection("users").document(userIdInput);
        return dRef;
    }


    public OnlineDatabase() {

    }
}