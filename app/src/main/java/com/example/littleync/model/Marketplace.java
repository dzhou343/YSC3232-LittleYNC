package com.example.littleync.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private final Context context;
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
    public Marketplace(Context context, ArrayList<Trade> trades) {
        this.context = context;
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

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete a User's active trade
     *
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     */
    public synchronized void deleteTrade(final FirebaseFirestore fs, final String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            deletingTrade = true;

            String userID = FirebaseAuth.getInstance().getUid();
            assert userID != null;
            final DocumentReference userDoc = fs.collection("users").document(userID);
            userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final User user = documentSnapshot.toObject(User.class);
                    assert user != null;

                    // Delete trade from User
                    user.removeTrade(tradeDocumentID);

                    // Delete trade from DB collection
                    fs.collection("trades").document(tradeDocumentID)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.writeToDatabaseDirectly(userDoc);
                                    showToast("Trade successfully deleted");
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
                }
            });
        } else {
            showToast("Trades still being processed, please wait");
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
     * @param sellType    the resource being sold
     * @param receiveType the resource requested
     * @param sellQty     the amount of resource being sold
     * @param receiveQty  the amount of resource requested
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void postTrade(final FirebaseFirestore fs, final String sellType, final String receiveType, final int sellQty, final int receiveQty) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            postingTrade = true;

            if (sellQty < 0) {
                showToast("Sell qty must be > 0");
                postingTrade = false;
            } else if (receiveQty < 0) {
                showToast("Receive qty must be > 0");
                postingTrade = false;
            } else {
                String userID = FirebaseAuth.getInstance().getUid();
                assert userID != null;
                final DocumentReference userDoc = fs.collection("users").document(userID);
                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                        final User user = documentSnapshot.toObject(User.class);
                        assert user != null;

                        if (user.getTrades().size() < 5) {
                            switch (sellType) {
                                case "wood":
                                    if (user.getWood() >= sellQty) {
                                        // Deposit the resource the user wants to trade
                                        user.setWood(user.getWood() - sellQty);
                                    } else {
                                        // The user does not have enough to deposit
                                        showToast("Not enough wood to trade");
                                        postingTrade = false;
                                        return;
                                    }
                                    break;
                                case "fish":
                                    if (user.getFish() >= sellQty) {
                                        user.setFish(user.getFish() - sellQty);
                                    } else {
                                        showToast("Not enough fish to trade");
                                        postingTrade = false;
                                        return;
                                    }
                                    break;
                                default:
                                    if (user.getGold() >= sellQty) {
                                        user.setGold(user.getGold() - sellQty);
                                    } else {
                                        showToast("Not enough gold to trade");
                                        postingTrade = false;
                                        return;
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
                                            user.writeToDatabaseDirectly(userDoc);

                                            trades.add(0, newTrade);
                                            tradesMap.put(newTrade.getDocumentID(), newTrade);

                                            showToast("Trade posted!");
                                            postingTrade = false;
                                        }
                                    });
                        } else {
                            // The user already has 5 live trades, which is the max
                            // Return false to display that the trade was unsuccessful
                            showToast("Cannot have more than 5 live trades");
                            postingTrade = false;
                        }
                    }
                });
            }
        } else {
            showToast("Trades still being processed, please wait");
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
     * @param fs              the current Firestore instance
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     */
    public synchronized void acceptTrade(final FirebaseFirestore fs, final TextView acceptButton, final String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            acceptingTrade = true;

            final Trade toAccept = tradesMap.get(tradeDocumentID);
            if (toAccept == null) {
                // Trade must not have been accepted before
                showToast("Already accepted this trade!");
                acceptingTrade = false;
            } else {
                final String sellType = toAccept.getSellType();
                final String receiveType = toAccept.getReceiveType();
                final int sellQty = toAccept.getSellQty();
                final int receiveQty = toAccept.getReceiveQty();

                String userID = FirebaseAuth.getInstance().getUid();
                assert userID != null;
                final DocumentReference userDoc = fs.collection("users").document(userID);
                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                        final User buyer = documentSnapshot.toObject(User.class);
                        assert buyer != null;

                        switch (receiveType) {
                            case "wood":
                                if (buyer.getWood() >= receiveQty) {
                                    // Less off the resource from the accepting user
                                    buyer.setWood(buyer.getWood() - receiveQty);
                                } else {
                                    // Accepting user does not have enough resources to trade
                                    acceptButton.setBackgroundColor(Color.RED);
                                    showToast("Not enough wood to trade");
                                    acceptingTrade = false;
                                    return;
                                }
                                break;
                            case "fish":
                                if (buyer.getFish() >= receiveQty) {
                                    buyer.setFish(buyer.getFish() - receiveQty);
                                } else {
                                    acceptButton.setBackgroundColor(Color.RED);
                                    showToast("Not enough fish to trade");
                                    acceptingTrade = false;
                                    return;
                                }
                                break;
                            default:
                                if (buyer.getGold() >= receiveQty) {
                                    buyer.setGold(buyer.getGold() - receiveQty);
                                } else {
                                    acceptButton.setBackgroundColor(Color.RED);
                                    showToast("Not enough gold to trade");
                                    acceptingTrade = false;
                                    return;
                                }
                                break;
                        }
                        // Trade is possible
                        // Debit the resource of the seller user
                        updateSellerResource(fs, acceptButton, toAccept);
                        // Debit the resource of the buyer
                        if (sellType.equals("wood")) {
                            buyer.addWood(sellQty);
                        } else if (sellType.equals("fish")) {
                            buyer.addFish(sellQty);
                        } else {
                            buyer.addGold(sellQty);
                        }
                        buyer.writeToDatabaseDirectly(userDoc);

                        // Remove the trade from the Map of live trades
                        trades.remove(toAccept);
                        tradesMap.remove(tradeDocumentID);
                    }
                });
            }
        } else {
            showToast("Trades still being processed, please wait");
        }
    }

    /**
     * Helper method invoked by acceptTrade() to update the seller's attributes in the DB as well
     * as delete the accepted trade off of the trades collection
     *
     * @param fs       the current Firestore instance
     * @param toAccept the Trade object that is being accepted
     */
    public synchronized void updateSellerResource(final FirebaseFirestore fs, final TextView acceptButton, final Trade toAccept) {
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
                                    seller.addWood(receiveQty);
                                } else if (receiveType.equals("fish")) {
                                    seller.addFish(receiveQty);
                                } else {
                                    seller.addGold(receiveQty);
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
                                                String success = "Done!";
                                                acceptButton.setText(success);
                                                showToast("Trade accepted!");
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
