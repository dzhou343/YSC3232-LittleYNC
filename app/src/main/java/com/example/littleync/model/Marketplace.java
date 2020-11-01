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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Marketplace class to handle the creation, posting, and accepting of trades from User to User;
 * only created for the Marketplace Activity
 */
public class Marketplace {
    private final String TAG = "Marketplace Class";
    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private ArrayList<Trade> trades;
    private Map<String, Trade> tradesMap = new HashMap<String, Trade>();
    private volatile Boolean acceptingTrade = false;
    private volatile Boolean postingTrade = false;

    /**
     * Constructor for the Marketplace object, which takes in the list of Trade objects that the
     * current session of the Marketplace Activity; this gets called in onCreate(); also saves down
     * the trades into a Map for ease of access
     *
     * @param trades an ArrayList of the 100 most recent trades
     */
    public Marketplace(ArrayList<Trade> trades) {
        this.trades = trades;
        for (Trade t : trades) {
            tradesMap.put(t.getDocumentID(), t);
        }
    }

    /**
     * Getter for the accessible trades for this session, used to return the trades for the
     * front-end scrollview to display
     *
     * @return ArrayList of active trades to display
     */
    public ArrayList<Trade> getTrades() {
        return trades;
    }

    /**
     * Getter for a specific User's live trades
     *
     * @param user the User who's trades we want to show
     * @return ArrayList of the User's trades to display
     */
    public ArrayList<Trade> getUserTrades(User user) {
        ArrayList<Trade> userTrades = new ArrayList<Trade>();
        for (String tradeDocumentID : user.getTrades()) {
            userTrades.add(tradesMap.get(tradeDocumentID));
        }
        return userTrades;
    }

    /**
     * Method called for a User to post a new trade; we need to process this locally in the current
     * User object as well as add the new trade to the trades collection in the DB; this will
     * return false if the user input negative numbers, if the User does not have enough resources,
     * or if previous trade actions are still being processed; when this method is invoked, it sets
     * a flag, postingTrade, that only resolves once this trade is entirely finished being
     * processed (logically and physically in DB)
     *
     * @param user the User posting the trade
     * @param sellType the resource being sold
     * @param receiveType the resource requested
     * @param sellQty the amount of resource being sold
     * @param receiveQty the amount of resource requested
     * @return true if the trade was successfully posted
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean postTrade(final User user, final String sellType, final String receiveType, final int sellQty, final int receiveQty) {
        if (!postingTrade && !acceptingTrade) {
            postingTrade = true;
            // Qty must be positive
            if (sellQty < 0 || receiveQty < 0) {
                return false;
            }
            if (user.getTrades().size() < 5) {
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
                fs.collection("trades")
                        .add(new Trade())
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String documentID = documentReference.getId();
                                Trade newTrade = new Trade(documentID, user.getUserName(), sellType, receiveType, sellQty, receiveQty, LocalDateTime.now().toString());
                                DocumentReference tradeDoc = fs.collection("trades").document(documentID);
                                newTrade.writeToDatabase(tradeDoc);
                                // Add the trade to the user's live trades
                                user.addTrade(documentID);
                                Log.d(TAG, "User trades size: " + user.getTrades().size());
                                Log.d(TAG, "Wrote to DB, posted trade");
                                postingTrade = false;
                            }
                        });
                // Return true to display that the trade was successfully posted
                return true;
            } else {
                // The user already has 5 live trades, which is the max
                // Return false to display that the trade was unsuccessful
                return false;
            }
        } else {
            Log.d(TAG, "Trades still being processed.");
            return false;
        }
    }

    /**
     * Method called for a User (the buyer) to accept a trade; we need to process this locally in
     * the current User object as well as for the person who posted the trade (the seller), finally
     * we also need to delete this trade from the trades collection in the DB; this will return
     * false if the buyer does not have enough enough resources, or if previous trade actions are
     * still being processed; when this method is invoked, it sets a flag, acceptingTrade, that
     * only resolves once this trade is entirely finished being processed (logically and physically
     * in DB)
     *
     * @param buyer the User object that is accepting the trade
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     * @return true if the trade was successfully accepted
     */
    public Boolean acceptTrade(User buyer, String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade) {
            acceptingTrade = true;

            Trade toAccept = tradesMap.get(tradeDocumentID);
            String sellType = toAccept.getSellType();
            String receiveType = toAccept.getReceiveType();
            int sellQty = toAccept.getSellQty();
            int receiveQty = toAccept.getReceiveQty();

            switch (receiveType) {
                case "wood":
                    if (buyer.getWood() >= receiveQty) {
                        // Less off the resource from the accepting user
                        buyer.setWood(buyer.getWood() - receiveQty);
                        // Debit the resource received
                        if (sellType.equals("wood")) {
                            buyer.setWood(buyer.getWood() + sellQty);
                        } else if (sellType.equals("fish")) {
                            buyer.setFish(buyer.getFish() + sellQty);
                        } else {
                            buyer.setGold(buyer.getGold() + sellQty);
                        }
                    } else {
                        // Accepting user does not have enough resources to trade
                        return false;
                    }
                    break;
                case "fish":
                    if (buyer.getFish() >= receiveQty) {
                        buyer.setFish(buyer.getFish() - receiveQty);
                        if (sellType.equals("wood")) {
                            buyer.setWood(buyer.getWood() + sellQty);
                        } else if (sellType.equals("fish")) {
                            buyer.setFish(buyer.getFish() + sellQty);
                        } else {
                            buyer.setGold(buyer.getGold() + sellQty);
                        }
                    } else {
                        return false;
                    }
                    break;
                default:
                    if (buyer.getGold() >= receiveQty) {
                        buyer.setGold(buyer.getGold() - receiveQty);
                        if (sellType.equals("wood")) {
                            buyer.setWood(buyer.getWood() + sellQty);
                        } else if (sellType.equals("fish")) {
                            buyer.setFish(buyer.getFish() + sellQty);
                        } else {
                            buyer.setGold(buyer.getGold() + sellQty);
                        }
                    } else {
                        return false;
                    }
                    break;
            }
            // Trade is completed
            // Debit the resource of the seller user
            updateSellerResource(toAccept);
            return true;
        } else {
            Log.d(TAG, "Trades still being processed.");
            return false;
        }
    }

    /**
     * Helper method invoked by acceptTrade() to update the seller's attributes in the DB as well
     * as delete the accepted trade off of the trades collection
     *
     * @param toAccept the Trade object that is being accepted
     */
    public void updateSellerResource(final Trade toAccept) {
        final String receiveType = toAccept.getReceiveType();
        final int receiveQty = toAccept.getReceiveQty();
        final String userName = toAccept.getUserName();
        final String documentID = toAccept.getDocumentID();

        Query queriedSellerUser = fs.collection("users")
                .whereEqualTo("userName", userName);

        queriedSellerUser
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // TODO userName has to be unique
                            // userName is unique so there is only one here
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Update seller User object
                                User seller = document.toObject(User.class);
                                String sellerUserID = document.getId();
                                // Delete trade from seller log
                                seller.removeTrade(documentID);
                                // Debit the resource being received
                                if (receiveType.equals("wood")) {
                                    seller.setWood(seller.getWood() + receiveQty);
                                } else if (receiveType.equals("fish")) {
                                    seller.setFish(seller.getFish() + receiveQty);
                                } else {
                                    seller.setGold(seller.getGold() + receiveQty);
                                }
                                // Write updated seller to DB
                                DocumentReference sellerDoc = fs.collection("users").document(sellerUserID);
                                seller.writeToDatabaseDirectly(sellerDoc);

                                // Delete the trade from trades collection
                                fs.collection("trades").document(documentID)
                                        .delete()
                                        .addOnSuccessListener(
                                                new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Trade successfully deleted");
                                                        acceptingTrade = false;
                                                    }
                                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting Trade", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting seller: ", task.getException());
                        }
                    }
                });
    }

}
