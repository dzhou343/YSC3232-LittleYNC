package com.example.littleync;

import com.example.littleync.model.Marketplace;
import com.example.littleync.model.Resource;

import android.os.Build;
import android.os.Bundle;
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
    private DocumentReference userDoc;
    private User user;
    private User initialUser;
    private volatile boolean userLoaded = false;
    private volatile boolean tradesLoaded = false;

    // For trading
    private Marketplace MARKETPLACE;

    //    s-: sell; b-: buy
    Resource sRecourceType;
    private EditText receiveQty;
    private EditText giveQty;
    private Button postDealBtn;
    private Button displayed;
    private Boolean posted;

    //    for trade button t
    private Boolean clicked;
    private Boolean finished;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page2);

//        T1 initialization
//        set up the spinner for the type of resource the user is asking for
        final Spinner receiveType = (Spinner) findViewById(R.id.receive_type);
        ArrayAdapter<String> receiveAdapter = new ArrayAdapter<String>(MarketplaceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.marketplace1_spinner));
        receiveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveType.setAdapter(receiveAdapter);
        receiveType.setOnItemSelectedListener(this);

//        set up the spinner for the type of resource the user is trading with
        final Spinner giveType = (Spinner) findViewById(R.id.give_type);
        ArrayAdapter<String> giveAdapter = new ArrayAdapter<String>(MarketplaceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.marketplace1_spinner));
        giveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        giveType.setAdapter(giveAdapter);
        giveType.setOnItemSelectedListener(this);

        receiveQty = (EditText) findViewById(R.id.receive_qty);
        giveQty = (EditText) findViewById(R.id.give_qty);

        postDealBtn = (Button) findViewById(R.id.post_deal_btn);
//        postDealBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (posted) {
//                    postDealBtn.setBackgroundColor(Color.TRANSPARENT);
//                    postDealBtn.setText("Posting");
//                    postDealBtn.setTextColor(getApplication().getResources().getColor(R.color.marketplace1_btn));
//                    posted = false;
//                } else {
//                    try {int s = Integer.parseInt(receiveQty.toString());} catch (Exception e) {Log.e("What the hell", e.getMessage());}
//                    postDeal(receiveType.toString(), Integer.parseInt(receiveQty.toString()), giveType.toString(), Integer.parseInt(giveQty.toString()));}
//            }
//    });
//        posted = false;

        final Button displayExistingTradesBtn = findViewById(R.id.display_existing_trades_btn);
        this.displayed = displayExistingTradesBtn;
//       TODO: check boolean if_displayed
        // Populate the scrollview on-demand
        displayExistingTradesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateExistingDeals();
            }
        });

        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        tradesLoaded = false;
        assert userID != null;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());

        // Setup marketplace for trading
        readTrades();
    }

    /**
     * Write the local User and any updates made to it back to the DB
     * This is called when we press the back button to return to the Main Activity
     */
    @Override
    public void onDestroy() {
        user.writeToDatabase(userDoc, initialUser);
        Log.d(TAG, "Wrote to DB");
        super.onDestroy();
    }

    public void readTrades() {
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
                            MARKETPLACE = new Marketplace(trades);
                            tradesLoaded = true;
                        }
                    }
                });
    }

    /**
     * Read in User by userID, update all the textViews at top of page
     *
     * @param ds
     */
    public void readUser(Task<DocumentSnapshot> ds) {
        ds.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        // Store the initial values of the user
                                        initialUser = documentSnapshot.toObject(User.class);
                                        // Store the user that this page will manipulate
                                        user = documentSnapshot.toObject(User.class);
                                        userLoaded = true;
                                    }
                                }
        );
    }

    // err I don't think this does anything. delete?
    public void tradePage(View view) {
        Log.d(TAG, TAG);
        FirebaseAuth fb = FirebaseAuth.getInstance();
        Log.d(TAG, fb.getCurrentUser().getUid().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postTrade(View view) {
        if (userLoaded && tradesLoaded) {
            ////        TODO: cast the dropdown option to the proper resource type
////        sRecourceType = Resource.Fish;
//
////        TODO: cast the edittext input to int
//
//        int sQty = 5;
//        int sBill = 10;
//
////        TODO: click the green button -> trigger the posting?
//
            boolean tryPost = MARKETPLACE.postTrade(user, "fish", "gold", 0, 11);
            if (tryPost) {
                String msg = "Successfully posted! Post another deal?";
                postDealBtn.setText(msg);
                postDealBtn.setBackgroundResource(R.drawable.marketplace2_btn);
                postDealBtn.setTextColor(0xff0000);
            } else {
                Toast fail = Toast.makeText(this, "Failed to post trade", Toast.LENGTH_LONG);
                fail.show();
            }
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    public void acceptTrade(Trade toAccept) {
        if (userLoaded && tradesLoaded) {
            boolean tryAccept = MARKETPLACE.acceptTrade(user, toAccept.getDocumentID());
            if (tryAccept) {
                Toast success = Toast.makeText(this, user.getUserName() + " accepted " + toAccept.getUserName() + "'s trade", Toast.LENGTH_LONG);
                success.show();
            } else {
                Toast fail = Toast.makeText(this, "Failed to accept trade", Toast.LENGTH_LONG);
                fail.show();
            }
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void populateExistingDeals() {
        if (userLoaded && tradesLoaded) {
            ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
            int lastRowID = R.id.first_row;

            for (int i = 0; i < MARKETPLACE.getTrades().size(); i++) {
                final Trade t = MARKETPLACE.getTrades().get(i);
                final View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
                TextView index = (TextView) new_row.findViewById(R.id.index2);
                TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
                TextView username = (TextView) new_row.findViewById(R.id.username2);
                TextView giving = (TextView) new_row.findViewById(R.id.giving2);
                TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
                final ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);
                final Button t2Btn_text = (Button) new_row.findViewById(R.id.t2_btn_text2);

                boolean sameUser = false;
                if (t.getUserName().equals(user.getUserName())) {
                    sameUser = true;
                }

//            change the appearance of the accept button if the current user posted this particular
//            trade and make it unclickable as well
                if (sameUser) {
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
                        if (finalSameUser) {
                            return;
                        }
                        acceptTrade(t);
                        t2Btn_text.setText("Done!");
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
                Log.d("BRO", String.valueOf(id));
                new_row.setId(id);

                ConstraintSet set = new ConstraintSet();
                set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
                set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);

                set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(id, ConstraintSet.TOP, lastRowID, ConstraintSet.BOTTOM, 0);
                set.applyTo(scrollParent);

                lastRowID = id;
            }
        } else {
            Toast populatingFail = Toast.makeText(this, "Failed to load trades.", Toast.LENGTH_LONG);
            populatingFail.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}


