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
import java.util.Objects;

/**
 * Marketplace class to handle the creation, posting, and accepting of trades from User to User;
 * only created for the Marketplace Activity
 */
public class Marketplace {
    private final String TAG = "Marketplace Class";
    private final ArrayList<Trade> trades;
    private final Map<String, Trade> tradesMap = new HashMap<>();
    private volatile boolean acceptingTrade = false;
    private volatile boolean postingTrade = false;
    private volatile boolean deletingTrade = false;

    /**
     * Constructor for the Marketplace object, which takes in the list of Trade objects that the
     * current session of the Marketplace Activity; this gets called in onCreate(); also saves down
     * the trades into a Map for ease of access
     *
     * @param trades an ArrayList of all trades
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
    public synchronized ArrayList<Trade> getTrades() {
        return trades;
    }

    /**
     * Getter for a specific User's live trades
     *
     * @param user the User whose trades we want to show
     * @return ArrayList of the User's trades to display
     */
    public synchronized ArrayList<Trade> getUserTrades(User user) {
        ArrayList<Trade> userTrades = new ArrayList<>();
        for (String tradeDocumentID : user.getTrades()) {
            userTrades.add(tradesMap.get(tradeDocumentID));
        }
        return userTrades;
    }

    /**
     * Delete a User's active trade
     *
     * @param user            User to remove trade from
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     */
    public synchronized void deleteTrade(final FirebaseFirestore fs, final DocumentReference userDoc, final User initialUser, final User user, String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            deletingTrade = true;
            // Delete trade from User
            user.removeTrade(tradeDocumentID);
            // Delete the trade from trades collection
            fs.collection("trades").document(tradeDocumentID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            user.writeToDatabase(fs, userDoc, initialUser);
                            Log.d(TAG, "Trade successfully deleted");
                            deletingTrade = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting Trade", e);
                            deletingTrade = false;
                        }
                    });
        } else {
            Log.d(TAG, "Trades still being processed.");
        }
    }

    /**
     * Method called for a User to post a new trade; we need to process this locally in the current
     * User object as well as add the new trade to the trades collection in the DB; this will
     * return a helpful message if the user input negative numbers, if the User does not have
     * enough resources, or if previous trade actions are still being processed; when this method
     * is invoked, it sets a flag, postingTrade, that only resolves once this trade is entirely
     * finished being processed (logically and physically in DB)
     *
     * @param fs          the current Firestore instance
     * @param user        the User posting the trade
     * @param sellType    the resource being sold
     * @param receiveType the resource requested
     * @param sellQty     the amount of resource being sold
     * @param receiveQty  the amount of resource requested
     * @return message to display to user what went right/wrong
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized String postTrade(final FirebaseFirestore fs, final DocumentReference userDoc, final User initialUser, final User user, final String sellType, final String receiveType, final int sellQty, final int receiveQty) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            // Qty must be positive
            if (sellQty < 0) {
                return "Sell qty must be > 0";
            } else if (receiveQty < 0) {
                return "Receive qty must be > 0";
            } else if (user.getTrades().size() < 5) {
                switch (sellType) {
                    case "wood":
                        if (user.getWood() >= sellQty) {
                            // Deposit the resource the user wants to trade
                            user.setWood(user.getWood() - sellQty);
                        } else {
                            // The user does not have enough to deposit
                            return "Not enough wood to trade";
                        }
                        break;
                    case "fish":
                        if (user.getFish() >= sellQty) {
                            user.setFish(user.getFish() - sellQty);
                        } else {
                            return "Not enough fish to trade";
                        }
                        break;
                    default:
                        if (user.getGold() >= sellQty) {
                            user.setGold(user.getGold() - sellQty);
                        } else {
                            return "Not enough gold to trade";
                        }
                        break;
                }
                // If the user has enough of the resource to deposit, then we can
                // proceed with physically processing the trade
                // Write trade to DB
                postingTrade = true;
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
                                trades.add(0, newTrade);
                                Log.d(TAG, "User trades size: " + user.getTrades().size());
                                user.writeToDatabase(fs, userDoc, initialUser);
                                Log.d(TAG, "Wrote to DB, posted trade");
                                postingTrade = false;
                            }
                        });
                // Return true to display that the trade was successfully posted
                return "Trade successfully posted!";
            } else {
                // The user already has 5 live trades, which is the max
                // Return false to display that the trade was unsuccessful
                return "Cannot have more than 5 live trades";
            }
        } else {
            return "Trades still being processed, please wait";
        }
    }

    /**
     * Method called for a User (the buyer) to accept a trade; we need to process this locally in
     * the current User object as well as for the person who posted the trade (the seller), finally
     * we also need to delete this trade from the trades collection in the DB; this will return
     * a helpful message if the buyer does not have enough enough resources, or if previous trade
     * actions are still being processed; when this method is invoked, it sets a flag,
     * acceptingTrade, that only resolves once this trade is entirely finished being processed
     * (logically and physically in DB)
     *
     * @param fs          the current Firestore instance
     * @param buyer           the User object that is accepting the trade
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     * @return message to display to user what went right/wrong
     */
    public synchronized String acceptTrade(final FirebaseFirestore fs, final DocumentReference userDoc, final User initialUser, User buyer, String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            Trade toAccept = tradesMap.get(tradeDocumentID);
            if (toAccept == null) {
                // Trade must not have been accepted before
                return "Already accepted trade!";
            } else if (toAccept.getUserName().equals(buyer.getUserName())) {
                // User cannot accept their own trade
                return "Cannot accept your own trade";
            } else {
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
                            return "Not enough wood to trade";
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
                            return "Not enough fish to trade";
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
                            return "Not enough gold to trade";
                        }
                        break;
                }
                // Trade is completed
                // Debit the resource of the seller user
                acceptingTrade = true;
                updateSellerResource(fs, toAccept);
                // Remove the trade from the Map of live trades
                trades.remove(toAccept);
                tradesMap.remove(tradeDocumentID);
                buyer.writeToDatabase(fs, userDoc, initialUser);
                return "Trade successfully accepted!";
            }
        } else {
            return "Trades still being processed, please wait";
        }
    }

    /**
     * Helper method invoked by acceptTrade() to update the seller's attributes in the DB as well
     * as delete the accepted trade off of the trades collection
     *
     * @param fs          the current Firestore instance
     * @param toAccept the Trade object that is being accepted
     */
    public synchronized void updateSellerResource(final FirebaseFirestore fs, final Trade toAccept) {
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
                            // userName is unique so there is only one here
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
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
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                acceptingTrade = false;
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting seller: ", task.getException());
                            acceptingTrade = false;
                        }
                    }
                });
    }

}
