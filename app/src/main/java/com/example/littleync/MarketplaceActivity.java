package com.example.littleync;

import com.example.littleync.model.Marketplace;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.example.littleync.model.Trade;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // To print to log instead of console
    private final static String TAG = "MarketplaceActivity";

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private User user;
    private volatile boolean userLoaded = false;
    private volatile boolean tradesLoaded = false;

    // For trading
    private Marketplace MARKETPLACE;

    //        T1
    private EditText receiveQty;
    private EditText sellQty;
    private Button postTradeBtn;
    private Boolean displayed;
    private Boolean posted;
    private Spinner receiveType;
    private Spinner sellType;
    private String receiveTypeStr;
    private String sellTypeStr;

    //    T2
    private ConstraintLayout scrollParent;
    private Boolean deleteConfirm = false;
    private String lastTimestamp;
    //    for trade button t


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page2);

//        T1 initialization
//        set up the spinner for the type of resource the user is asking for
        receiveType = findViewById(R.id.receive_type);
        ArrayAdapter<String> receiveAdapter = new ArrayAdapter<>(MarketplaceActivity.this,
                R.layout.marketplace_spinner_default, getResources().getStringArray(R.array.marketplace1_spinner));
        receiveAdapter.setDropDownViewResource(R.layout.marketplace_spinner_dropdown);
        receiveType.setAdapter(receiveAdapter);
        receiveType.setOnItemSelectedListener(this);

//        set up the spinner for the type of resource the user is trading with
        sellType = findViewById(R.id.sell_type);
        ArrayAdapter<String> sellAdapter = new ArrayAdapter<>(MarketplaceActivity.this,
                R.layout.marketplace_spinner_default, getResources().getStringArray(R.array.marketplace1_spinner));
        sellAdapter.setDropDownViewResource(R.layout.marketplace_spinner_dropdown);
        sellType.setAdapter(sellAdapter);
        sellType.setOnItemSelectedListener(this);

        receiveQty = findViewById(R.id.receive_qty);
        sellQty = findViewById(R.id.sell_qty);

        postTradeBtn = findViewById(R.id.post_trade_btn);
        postTradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int receiveQtyInt = Integer.parseInt(receiveQty.getText().toString());
                    int sellQtyInt = Integer.parseInt(sellQty.getText().toString());
                    postTrade(sellTypeStr, receiveTypeStr, sellQtyInt, receiveQtyInt);
                } catch (Exception e) {
                    Log.e("What the hell", e.getMessage());
                }

//                TODO: delete button, dropdown appearance, automatic refresh
            }
        });

        posted = false;

        scrollParent = findViewById(R.id.scroll_parent);
        final Button displayExistingTradesBtn = findViewById(R.id.display_existing_trades_btn);
        this.displayed = false;

        // Populate the scrollview on-demand
        displayExistingTradesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayed = true;
                scrollParent.removeAllViews();
                readUserAndPopulateTrades();
            }
        });

        // Setup marketplace for trading
        readUserAndPopulateTrades();
    }

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

                Query queriedTrades = fs.collection("trades")
                        .orderBy("timeOfListing", Query.Direction.DESCENDING);
                queriedTrades
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Trade> trades = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Trade t = document.toObject(Trade.class);
                                        trades.add(t);
                                    }
                                    MARKETPLACE = new Marketplace(getApplicationContext(), getLayoutInflater(), trades);
                                    tradesLoaded = true;
                                    populateExistingDeals();
                                }
                            }
                        });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public synchronized void postTrade(String sellType, String receiveType, int sellQty, int receiveQty) {
        if (userLoaded && tradesLoaded) {
            MARKETPLACE.postTrade(fs, sellType, receiveType, sellQty, receiveQty);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    public synchronized void acceptTrade(TextView acceptButton, String documentID) {
        if (userLoaded && tradesLoaded) {
            MARKETPLACE.acceptTrade(fs, acceptButton, documentID);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    public synchronized void deleteTrade(TextView deleteButton, String documentID) {
        if (userLoaded && tradesLoaded) {
            MARKETPLACE.deleteTrade(fs, deleteButton, documentID);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected synchronized void populateExistingDeals() {

        int lastRowID = 0;

        if (userLoaded && tradesLoaded) {
            for (int i = 0; i < MARKETPLACE.getTrades().size(); i++) {
                final Trade t = MARKETPLACE.getTrades().get(i);
                final View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
                TextView index = new_row.findViewById(R.id.index2);
                final TextView timestamp = new_row.findViewById(R.id.timestamp2);
                TextView username = new_row.findViewById(R.id.armory_toast_msg);
                TextView giving = new_row.findViewById(R.id.giving2);
                TextView receiving = new_row.findViewById(R.id.receiving2);
                final ImageButton t2Btn = new_row.findViewById(R.id.t2_btn2);
                final Button t2Btn_text = new_row.findViewById(R.id.t2_btn_text2);
                final Button t2Btn_text_delete = new_row.findViewById(R.id.t2_btn_text3);

//            change the appearance of the accept button if the current user posted this particular
//            trade and make it unclickable as well
                boolean sameUser = false;
                if (t.getUserName().equals(user.getUserName())) {
                    sameUser = true;
                    t2Btn.setVisibility(View.INVISIBLE);
                    t2Btn_text.setVisibility(View.VISIBLE);
                    t2Btn_text.setText("Your trade");
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

//                                to debug the toast message customization
                            LayoutInflater inflater = getLayoutInflater();
                            View toastLayout = inflater.inflate(R.layout.toast_custom, null);


                        }
                    }
                });

                t2Btn_text_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t2Btn_text.setText("Deleted!");
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

//        add to parent
                scrollParent.addView(new_row);

//        set id & then constraints
                int id = View.generateViewId();
//                Log.d("BRO", String.valueOf(id));
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.receive_type:
                receiveTypeStr = parent.getItemAtPosition(position).toString();
                break;
            case R.id.sell_type:
                sellTypeStr = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}


