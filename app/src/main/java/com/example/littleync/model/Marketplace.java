package com.example.littleync.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Marketplace {
    private String TAG = "Marketplace Class";
    public Map<String, Trade> tradesMap;
    private volatile Boolean tradeAccepted = false;
    private volatile Boolean tradePosted = false;
    private DocumentReference tradeDoc;
    FirebaseFirestore fs = FirebaseFirestore.getInstance();

    public Marketplace(ArrayList<Trade> trades) {
//        for (Trade t : trades) {
//            tradesMap.put(t.getDocumentID(), t);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean postTrade(final User user, final String sellType, final String receiveType, final int sellQty, final int receiveQty) {
//        tradePosted = false;
        int liveTrades = user.getTrades().size();
        // Qty must be positive
        if (sellQty <= 0 || receiveQty <= 0) {
            return false;
        }
        if (liveTrades < 5) {
            switch (sellType) {
                case "wood":
                    if (user.getWood() >= sellQty) {
                        // Deposit the resource the user wants to trade
                        user.setWood(user.getWood() - sellQty);
                    } else {
                        // The user does not have enough to deposit
                        return false;
                    }
                    break;
                case "fish":
                    if (user.getFish() >= sellQty) {
                        user.setFish(user.getFish() - sellQty);
                    } else {
                        return false;
                    }
                    break;
                default:
                    if (user.getGold() >= sellQty) {
                        user.setGold(user.getGold() - sellQty);
                    } else {
                        return false;
                    }
                    break;
            }
            // If the user has enough of the resource to deposit, then we can
            // proceed with physically processing the trade
            // Write trade to DB
            //Creates a new document, and then attaches the doc ID to documentID
            // TODO see if it works
            fs.collection("trades")
                    .add(new Trade())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String documentID = documentReference.getId();
                            Trade newTrade = new Trade(documentID, user.getUserName(), sellType, receiveType, sellQty, receiveQty, LocalDateTime.now());
                            tradeDoc = fs.collection("trades").document(documentID);
                            newTrade.writeToDatabase(tradeDoc);
                            Log.d(TAG, "Posted trade");
                            // Add the trade to the user's live trades
                            user.addTrade(documentID);
//                            tradePosted = true;
                        }
                    });
            // Return true to display that the trade was successfully posted
//            while (!tradePosted) {}
            return true;
        } else {
            // The user already has 5 live trades, which is the max
            // Return false to display that the trade was unsuccessful
            return false;
        }
    }

//    public void listLiveTrades() {
//        for (Trade t : trades.values()) {
//            System.out.println(t.getDocumentID());
//        }
//    }
//
//    public void listUserTrades(User user) {
//        for (String tradeID : user.getTrades()) {
//            System.out.println(trades.get(tradeID));
//        }
//    }

    public void updateSellerResource(final Trade completeTradeForSeller) {
        final String receiveResourceType = completeTradeForSeller.getReceiveType();
        final int receiveQuantity = completeTradeForSeller.getReceiveQty();
        final String userName = completeTradeForSeller.getUserName();

        Query queriedUserObject = fs.collection("users").whereEqualTo("userName", userName);
        queriedUserObject
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // TODO userName has to be unique
                            // userName is unique so there is only one here
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User seller = document.toObject(User.class);
                                if (seller.getUserName().equals(userName)) {
                                    // Delete trade from seller log
                                    seller.removeTrade(completeTradeForSeller.getDocumentID());
                                    // Debit the resource being received
                                    switch (receiveResourceType) {
                                        case ("wood"):
                                            seller.setWood(seller.getWood() + receiveQuantity);
                                            break;
                                        case ("fish"):
                                            seller.setFish(seller.getFish() + receiveQuantity);
                                            break;
                                        default:
                                            seller.setGold(seller.getGold() + receiveQuantity);
                                            break;
                                    }

                                    // Delete the trade from trades collection
                                    fs.collection("trades").document(completeTradeForSeller.getDocumentID())
                                            .delete()
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Trade successfully deleted");
                                                        }
                                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting Trade", e);
                                                }
                                            });
//                                    // Flag that the acceptTrade() method can complete
//                                    tradeAccepted = true;
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting user: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Buyer's user
     *
     * @param buyer      Seller's tradeID
     * @param documentID
     * @return
     */
    public Boolean acceptTrade(User buyer, String documentID) {
//        tradeAccepted = false;
        Trade sellerTrade = tradesMap.get(documentID);
        switch (sellerTrade.getReceiveType()) {
            case "wood":
                if (buyer.getWood() >= sellerTrade.getSellQty()) {
                    // Less off the resource from the accepting user
                    buyer.setWood(buyer.getWood() - sellerTrade.getSellQty());
                    // Debit the resource received
                    if (sellerTrade.getSellType().equals("wood")) {
                        buyer.setWood(buyer.getWood() + sellerTrade.getReceiveQty());
                    } else if (sellerTrade.getSellType().equals("fish")) {
                        buyer.setFish(buyer.getFish() + sellerTrade.getReceiveQty());
                    } else {
                        buyer.setGold(buyer.getGold() + sellerTrade.getReceiveQty());
                    }
                } else {
                    // Accepting user does not have enough resources to trade
                    return false;
                }
                break;
            case "fish":
                if (buyer.getFish() >= sellerTrade.getSellQty()) {
                    // Less off the resource from the accepting user
                    buyer.setFish(buyer.getFish() - sellerTrade.getSellQty());
                    // Debit the resource received
                    if (sellerTrade.getSellType().equals("wood")) {
                        buyer.setWood(buyer.getWood() + sellerTrade.getReceiveQty());
                    } else if (sellerTrade.getSellType().equals("fish")) {
                        buyer.setFish(buyer.getFish() + sellerTrade.getReceiveQty());
                    } else {
                        buyer.setGold(buyer.getGold() + sellerTrade.getReceiveQty());
                    }
                } else {
                    return false;
                }
                break;
            default:
                if (buyer.getGold() >= sellerTrade.getSellQty()) {
                    // Less off the resource from the accepting user
                    buyer.setGold(buyer.getGold() - sellerTrade.getSellQty());
                    // Debit the resource received
                    if (sellerTrade.getSellType().equals("wood")) {
                        buyer.setWood(buyer.getWood() + sellerTrade.getReceiveQty());
                    } else if (sellerTrade.getSellType().equals("fish")) {
                        buyer.setFish(buyer.getFish() + sellerTrade.getReceiveQty());
                    } else {
                        buyer.setGold(buyer.getGold() + sellerTrade.getReceiveQty());
                    }
                } else {
                    return false;
                }
                break;
        }
        // Trade is completed
        // Debit the resource of the accepted user
        updateSellerResource(sellerTrade);
//        while (!tradeAccepted) {}
        return true;
    }

}
