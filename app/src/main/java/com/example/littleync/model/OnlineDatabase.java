package com.example.littleync.model;

///The following packages below are in preparation for reading and writing
//import com.google.firebase.*;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.*;
import com.google.firebase.Timestamp;
import com.google.android.gms.tasks.*;

import java.util.Map;


public class OnlineDatabase {
    String userID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ///Function to read user data
    public void userRead(String userIdInput, String collection){
        this.userID = userIdInput;
        System.out.println(userIdInput + userID);
        DocumentReference dRef = db.collection(collection).document(userIdInput);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                             @Override
                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                 if (task.isSuccessful()) {
                                                     Map n = task.getResult().getData();
                                                     System.out.println(n.keySet());
                                                 }
                                             }
                                         }
        );
    }
    public void userUpdate(String userIdInput, String collection, String key, Object objToUpdate){
        this.userID = userIdInput;
        System.out.println(userIdInput + userID);
        DocumentReference dRef = db.collection(collection).document(userIdInput);
        dRef.update(key, objToUpdate);
    }
    public void userWrite(String userIdInput, String collection, Object objToUpdate){
        this.userID = userIdInput;
        System.out.println(userIdInput + userID);
        DocumentReference dRef = db.collection(collection).document(userIdInput);
        dRef.set(objToUpdate);
    }

    public OnlineDatabase() {

    }
}