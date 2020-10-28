package com.example.littleync.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OnlineDatabase
 *
 * Description: This class is the blueprint for querying and generating objects from the database
 *
 * userReadWrite()
 * @param
 *
 */
public class OnlineDatabase {
    //Auto instantiates a UID as long as there is someone logged in here.
    String userID = FirebaseAuth.getInstance().getUid();
    String USER_COLLECTION = "users";
    String TRADE_COLLECTION = "trades";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Trade> trades = new HashMap<String, Trade>();

    /**
     * Constructor for the class
     *
     * Description: the userID is taken from the FirebaseAuth object.
     *
     * @param
     */
    public OnlineDatabase() {

    }

    public DocumentReference userReadWrite() {
        return db.collection(USER_COLLECTION).document(userID);
    }

    public DocumentReference tradeReadWrite(String documentID) {
        return db.collection(TRADE_COLLECTION).document(documentID);
    }

    // ASYNC ISSUE
    public Map<String, Trade> readAllTrades() {
        trades.clear();
        CollectionReference tradesRef = db.collection(TRADE_COLLECTION);
        tradesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Trade t = document.toObject(Trade.class);
                        System.out.println(t.getTradeID());
                        trades.put(t.getTradeID(), t);
                    }
                }
            }
        });
        for (String t : trades.keySet()) {
            System.out.println(t);
            System.out.println("hi");
        }
        return trades;
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