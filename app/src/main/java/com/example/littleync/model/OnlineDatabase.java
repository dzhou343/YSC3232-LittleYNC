package com.example.littleync.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

public class OnlineDatabase {
    String userID;
    String USERCOLLECTION = "users";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OnlineDatabase(String userID) {
        this.userID = userID;
    }

    public DocumentReference userReadWrite() {
        return db.collection(USERCOLLECTION).document(userID);
    }

    ///Function to read user data
//    public void userRead(String userIdInput, String collection){
//        this.userID = userIdInput;
//        System.out.println(userIdInput + userID);
//        DocumentReference dRef = db.collection(collection).document(userIdInput);
//        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                             @Override
//                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                 if (task.isSuccessful()) {
//                                                     Map n = task.getResult().getData();
//                                                     System.out.println(n.keySet());
//                                                 }
//                                             }
//                                         }
//        );
//    }
//
//    public void userUpdate(String userIdInput, String collection, String key, Object objToUpdate){
//        this.userID = userIdInput;
//        System.out.println(userIdInput + userID);
//        DocumentReference dRef = db.collection(collection).document(userIdInput);
//        dRef.update(key, objToUpdate);
//    }
//
//    public void userWrite(String userIdInput, String collection, Object objToUpdate){
//        this.userID = userIdInput;
//        System.out.println(userIdInput + userID);
//        DocumentReference dRef = db.collection(collection).document(userIdInput);
//        dRef.set(objToUpdate);
//    }

}