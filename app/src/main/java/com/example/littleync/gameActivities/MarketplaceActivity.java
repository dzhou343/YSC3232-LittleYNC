package com.example.littleync.gameActivities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.littleync.R;
import com.example.littleync.model.Trade;
import com.example.littleync.model.User;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Marketplace Activity where the user can post and accept trades for resources from other users
 */
public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // To print to log instead of console
    private final static String TAG = "MarketplaceActivity";

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private User user;
    private volatile Boolean userLoaded = false;

    // To update User stats at top of page
    private TextView woodDisplay;
    private TextView woodchoppingGearLevelDisplay;
    private TextView fishDisplay;
    private TextView fishingGearLevelDisplay;
    private TextView goldDisplay;
    private TextView combatGearLevelDisplay;
    private TextView aggLevelDisplay;
    private TextView aggLevelProgressDisplay;

    // For trading
    private volatile boolean tradesLoaded = false;
    private final ArrayList<Trade> trades = new ArrayList<>();
    private final Map<String, Trade> tradesMap = new HashMap<>();
    private volatile boolean acceptingTrade = false;
    private volatile boolean postingTrade = false;
    private volatile boolean deletingTrade = false;

    // Trading UI elements
    private String sellTypeStr;
    private String receiveTypeStr;
    private EditText sellQty;
    private EditText receiveQty;
    private ConstraintLayout scrollParent;

    /**
     * Initialize the objects and TextViews required for this page, including ScrollView of trades
     *
     * @param savedInstanceState pass info around
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page2);

        // Setup marketplace for trading
        readUserAndPopulateTrades();

        // Identify the correct TextViews
        woodDisplay = findViewById(R.id.wood_res);
        woodchoppingGearLevelDisplay = findViewById(R.id.wood_gear_level);
        fishDisplay = findViewById(R.id.fish_res);
        fishingGearLevelDisplay = findViewById(R.id.fish_gear_level);
        goldDisplay = findViewById(R.id.gold_res);
        combatGearLevelDisplay = findViewById(R.id.combat_gear_level);
        aggLevelDisplay = findViewById(R.id.agg_level);
        aggLevelProgressDisplay = findViewById(R.id.agg_level_progress);

        // Set up the spinner for the type of resource the user is asking for
        Spinner receiveType = findViewById(R.id.receive_type);
        ArrayAdapter<String> receiveAdapter = new ArrayAdapter<>(MarketplaceActivity.this,
                R.layout.marketplace_spinner_default, getResources().getStringArray(R.array.marketplace1_spinner));
        receiveAdapter.setDropDownViewResource(R.layout.marketplace_spinner_dropdown);
        receiveType.setAdapter(receiveAdapter);
        receiveType.setOnItemSelectedListener(this);

        // Set up the spinner for the type of resource the user is trading with
        Spinner sellType = findViewById(R.id.sell_type);
        ArrayAdapter<String> sellAdapter = new ArrayAdapter<>(MarketplaceActivity.this,
                R.layout.marketplace_spinner_default, getResources().getStringArray(R.array.marketplace1_spinner));
        sellAdapter.setDropDownViewResource(R.layout.marketplace_spinner_dropdown);
        sellType.setAdapter(sellAdapter);
        sellType.setOnItemSelectedListener(this);

        receiveQty = findViewById(R.id.receive_qty);
        sellQty = findViewById(R.id.sell_qty);

        Button postTradeBtn = findViewById(R.id.post_trade_btn);
        postTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int receiveQtyInt = Integer.parseInt(receiveQty.getText().toString());
                    int sellQtyInt = Integer.parseInt(sellQty.getText().toString());
                    postTrade(sellTypeStr, receiveTypeStr, sellQtyInt, receiveQtyInt);
                } catch (Exception e) {
                    Log.e("What the heck", e.getMessage());
                }
//                TODO: delete button, dropdown appearance, automatic refresh
            }
        });

        scrollParent = findViewById(R.id.scroll_parent);
        final Button displayExistingTradesBtn = findViewById(R.id.display_existing_trades_btn);
        // Populate the scrollview on-demand
        displayExistingTradesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readUserAndPopulateTrades();
            }
        });
    }

    /**
     * Updates the current selection of resource to sell/receive
     *
     * @param parent   spinner
     * @param view     for Android
     * @param position for Android
     * @param id       for Android
     */
    @Override
    public synchronized void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final int receiveTypeSpinner = R.id.receive_type;
        final int sellTypeSpinner = R.id.sell_type;
        switch (parent.getId()) {
            case receiveTypeSpinner:
                receiveTypeStr = parent.getItemAtPosition(position).toString();
                break;
            case sellTypeSpinner:
                sellTypeStr = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    /**
     * Sets the default for the spinner
     *
     * @param parent spinner
     */
    @Override
    public synchronized void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Read in User by userID, update all the textViews at top of page, flags that the User has
     * been loaded in, then also reads in all trades, populates the ScrollView, flags that the
     * trades have been loaded in
     */
    public synchronized void readUserAndPopulateTrades() {
        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        tradesLoaded = false;
        assert userID != null;
        DocumentReference userDoc = fs.collection("users").document(userID);
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                // Store the user that this page will manipulate
                user = documentSnapshot.toObject(User.class);
                userLoaded = true;
                refreshUserAttributes();

                Query queriedTrades = fs.collection("trades")
                        .orderBy("timeOfListing", Query.Direction.DESCENDING);
                queriedTrades
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    trades.clear();
                                    tradesMap.clear();
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Trade t = document.toObject(Trade.class);
                                        trades.add(t);
                                        tradesMap.put(t.getDocumentID(), t);
                                    }
                                    tradesLoaded = true;
                                    populateExistingDeals();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Refreshes the TextViews that display the User attributes at the top of the page
     */
    protected void refreshUserAttributes() {
        String woodRes = String.format(Locale.getDefault(), "%s", user.getWood());
        woodDisplay.setText(woodRes);
        String woodGearLevel = String.format(Locale.getDefault(), "%s", user.getWoodchoppingGearLevel());
        woodchoppingGearLevelDisplay.setText(woodGearLevel);
        String fishRes = String.format(Locale.getDefault(), "%s", user.getFish());
        fishDisplay.setText(fishRes);
        String fishGearLevel = String.format(Locale.getDefault(), "%s", user.getFishingGearLevel());
        fishingGearLevelDisplay.setText(fishGearLevel);
        String goldRes = String.format(Locale.getDefault(), "%s", user.getGold());
        goldDisplay.setText(goldRes);
        String combatGearLevel = String.format(Locale.getDefault(), "%s", user.getCombatGearLevel());
        combatGearLevelDisplay.setText(combatGearLevel);
        String aggLevel = String.format(Locale.getDefault(), "LEVEL %s", user.getAggregateLevel());
        aggLevelDisplay.setText(aggLevel);
        String aggLevelProgress = String.format(Locale.getDefault(), "%s / %s", user.getExp(), user.requiredExperience(user.getAggregateLevel() + 1));
        aggLevelProgressDisplay.setText(aggLevelProgress);
    }

    /**
     * Re-populates the ScrollView with the trades read from the DB
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected synchronized void populateExistingDeals() {
        scrollParent.removeAllViews();
        int lastRowID = 0;

        if (userLoaded && tradesLoaded) {
            for (int i = 0; i < trades.size(); i++) {
                final Trade t = trades.get(i);
                @SuppressLint("InflateParams") final View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

                // Set content
                TextView index = new_row.findViewById(R.id.index2);
                final TextView timestamp = new_row.findViewById(R.id.timestamp2);
                TextView username = new_row.findViewById(R.id.armory_toast_msg);
                TextView giving = new_row.findViewById(R.id.giving2);
                TextView receiving = new_row.findViewById(R.id.receiving2);
                final ImageButton t2Btn = new_row.findViewById(R.id.t2_btn2);
                final Button t2Btn_text = new_row.findViewById(R.id.t2_btn_text2);
                final Button t2Btn_text_delete = new_row.findViewById(R.id.t2_btn_text3);

                // Change the appearance of the accept button if the current user posted this particular
                // trade and make it unclickable as well
                boolean sameUser = false;
                if (t.getUserName().equals(user.getUserName())) {
                    sameUser = true;
                    t2Btn.setVisibility(View.INVISIBLE);
                    t2Btn_text.setVisibility(View.VISIBLE);
                    String msg = "Your trade";
                    t2Btn_text.setText(msg);
                }

                index.setText(String.valueOf(i + 1));
                timestamp.setText(t.getTimeOfListing().split("T")[0]);
                username.setText(t.getUserName());
                giving.setText(String.format(Locale.getDefault(), "%s x %d", t.getSellType(), t.getSellQty()));
                receiving.setText(String.format(Locale.getDefault(), "%s x %d", t.getReceiveType(), t.getReceiveQty()));

                final boolean finalSameUser = sameUser;
                t2Btn_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!finalSameUser) {
                            acceptTrade(t2Btn_text, t.getDocumentID());
                        } else {
                            t2Btn_text.setVisibility(View.INVISIBLE);
                            t2Btn_text_delete.setVisibility(View.VISIBLE);
                        }
                    }
                });

                t2Btn_text_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "Deleted!";
                        t2Btn_text.setText(msg);
                        deleteTrade(t2Btn_text_delete, t.getDocumentID());
                    }
                });

                t2Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t2Btn.setVisibility(View.INVISIBLE);
                        t2Btn_text.setVisibility(View.VISIBLE);
                    }
                });

                // Add to parent
                scrollParent.addView(new_row);

                // Set id, then constraints
                int id = View.generateViewId();
                new_row.setId(id);

                ConstraintSet set = new ConstraintSet();
                set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
                set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);

                set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);

                if (i == 0) {
                    set.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                } else {
                    set.connect(id, ConstraintSet.TOP, lastRowID, ConstraintSet.BOTTOM, 0);
                }

                set.applyTo(scrollParent);
                lastRowID = id;
            }
        } else {
            Toast populatingFail = Toast.makeText(this, "Failed to load trades, try again", Toast.LENGTH_SHORT);
            populatingFail.show();
        }
    }

    /**
     * Check that the user and trades have been loaded in properly before proceeding to physically
     * process the trade in the DB
     *
     * @param sellType    the resource being sold
     * @param receiveType the resource requested
     * @param sellQty     the amount of resource being sold
     * @param receiveQty  the amount of resource requested
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void postTrade(String sellType, String receiveType, int sellQty, int receiveQty) {
        if (userLoaded && tradesLoaded) {
            postTradeDB(sellType, receiveType, sellQty, receiveQty);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    /**
     * Check that the user and trades have been loaded in properly before proceeding to physically
     * process the trade in the DB
     *
     * @param acceptButton the button to update the status of the trade
     * @param documentID   which refers to the trade in the DB
     */
    public synchronized void acceptTrade(TextView acceptButton, String documentID) {
        if (userLoaded && tradesLoaded) {
            acceptTradeDB(acceptButton, documentID);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    /**
     * Check that the user and trades have been loaded in properly before proceeding to physically
     * process the trade in the DB
     *
     * @param deleteButton the button to update the status of the trade
     * @param documentID   that corresponds to the documentID in the trades collection
     */
    public synchronized void deleteTrade(TextView deleteButton, String documentID) {
        if (userLoaded && tradesLoaded) {
            deleteTradeDB(deleteButton, documentID);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    /**
     * Convenient method that properly formats then displays a toast message
     *
     * @param msg what we want the toast to display
     */
    private void showToast(String msg) {
        @SuppressLint("InflateParams") View toastLayout = getLayoutInflater().inflate(R.layout.toast_custom, null);

        TextView text = toastLayout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * Processes the deletion of a User's active trade in the DB and returns the deposited amount
     * back to the user; once done, it automatically refreshes the user's attributes at the top of
     * the screen
     *
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     */
    public synchronized void deleteTradeDB(final TextView deleteButton, final String tradeDocumentID) {
        if (!postingTrade && !acceptingTrade && !deletingTrade) {
            deletingTrade = true;

            String userID = FirebaseAuth.getInstance().getUid();
            assert userID != null;
            final DocumentReference userDoc = fs.collection("users").document(userID);
            userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final User currentUser = documentSnapshot.toObject(User.class);
                    assert currentUser != null;

                    if (tradesMap.containsKey(tradeDocumentID)) {
                        // Delete trade from DB collection
                        fs.collection("trades").document(tradeDocumentID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Delete trade from User
                                        currentUser.removeTrade(tradeDocumentID);
                                        // Return what the user deposited
                                        Trade toDelete = tradesMap.get(tradeDocumentID);
                                        switch (Objects.requireNonNull(toDelete).getSellType()) {
                                            case "wood":
                                                currentUser.addWood(toDelete.getSellQty());
                                                break;
                                            case "fish":
                                                currentUser.addFish(toDelete.getSellQty());
                                                break;
                                            default:
                                                currentUser.addGold(toDelete.getSellQty());
                                                break;
                                        }
                                        // Remove from local store
                                        trades.remove(toDelete);
                                        tradesMap.remove(tradeDocumentID);

                                        // Write User back to DB
                                        currentUser.writeToDatabaseDirectly(userDoc);

                                        String del = "Deleted!";
                                        deleteButton.setText(del);
                                        showToast("Trade successfully deleted!");

                                        // Refresh attributes
                                        user = currentUser;
                                        refreshUserAttributes();
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
                        showToast("Trade already deleted!");
                        deletingTrade = false;
                    }
                }
            });
        } else {
            showToast("Trades still being processed, please wait");
        }
    }

    /**
     * Method called for a User to post a new trade; we need to add the new trade to the trades
     * collection in the DB; this will show a helpful toast if the user input negative numbers, if
     * the User does not have enough resources, or if previous trade actions are still being
     * processed; when this method is invoked, it sets a flag, postingTrade, that only resolves
     * once this trade is entirely finished being processed (logically and physically in DB); once
     * done, it also automatically refreshes the trade list
     *
     * @param sellType    the resource being sold
     * @param receiveType the resource requested
     * @param sellQty     the amount of resource being sold
     * @param receiveQty  the amount of resource requested
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void postTradeDB(final String sellType, final String receiveType, final int sellQty, final int receiveQty) {
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

                        final User seller = documentSnapshot.toObject(User.class);
                        assert seller != null;

                        if (seller.getTrades().size() < 5) {
                            switch (sellType) {
                                case "wood":
                                    if (seller.getWood() >= sellQty) {
                                        // Deposit the resource the user wants to trade
                                        seller.setWood(seller.getWood() - sellQty);
                                    } else {
                                        // The user does not have enough to deposit
                                        showToast("Not enough wood to trade");
                                        postingTrade = false;
                                        return;
                                    }
                                    break;
                                case "fish":
                                    if (seller.getFish() >= sellQty) {
                                        seller.setFish(seller.getFish() - sellQty);
                                    } else {
                                        showToast("Not enough fish to trade");
                                        postingTrade = false;
                                        return;
                                    }
                                    break;
                                default:
                                    if (seller.getGold() >= sellQty) {
                                        seller.setGold(seller.getGold() - sellQty);
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
                                            Trade newTrade = new Trade(documentID, seller.getUserName(), sellType, receiveType, sellQty, receiveQty, LocalDateTime.now().toString());
                                            DocumentReference tradeDoc = fs.collection("trades").document(documentID);
                                            newTrade.writeToDatabase(tradeDoc);

                                            // Add the trade to the user's live trades
                                            seller.addTrade(documentID);
                                            seller.writeToDatabaseDirectly(userDoc);

                                            trades.add(0, newTrade);
                                            tradesMap.put(newTrade.getDocumentID(), newTrade);

                                            showToast("Trade posted!");

                                            // Automatically reload trades
                                            readUserAndPopulateTrades();
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
     * Method called for a User (the buyer) to accept a trade; we need to process this for the
     * current user as well as for the person who posted the trade (the seller), finally we also
     * need to delete this trade from the trades collection in the DB; this will show a helpful
     * toast message if the buyer does not have enough enough resources, or if previous trade
     * actions are still being processed; when this method is invoked, it sets a flag,
     * acceptingTrade, that only resolves once this trade is entirely finished being processed
     * (logically and physically in DB); once done, it automatically refreshes the user's
     * attributes at the top of the screen
     *
     * @param tradeDocumentID that corresponds to the documentID in the trades collection
     */
    public synchronized void acceptTradeDB(final TextView acceptButton, final String tradeDocumentID) {
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
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document == null) {
                                                    showToast("User does not exist anymore");
                                                    String NA = "N/A";
                                                    acceptButton.setText(NA);
                                                    acceptButton.setBackgroundColor(Color.RED);
                                                }

                                                assert document != null;
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

                                                                // After all that, trade is successful
                                                                String success = "Done!";
                                                                acceptButton.setText(success);
                                                                showToast("Trade accepted!");

                                                                // Refresh attributes
                                                                user = buyer;
                                                                refreshUserAttributes();
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
                });
            }
        } else {
            showToast("Trades still being processed, please wait");
        }
    }

}


