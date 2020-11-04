package com.example.littleync;

import com.example.littleync.model.Marketplace;
import com.example.littleync.model.Resource;

import android.annotation.SuppressLint;
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

import static com.example.littleync.MainActivity.loginStatus;
import static com.example.littleync.MainActivity.logoutTrigger;

public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MarketplaceActivityInterface {
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

//        T1
    Resource sRecourceType;
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
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.marketplace1_spinner));
        sellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                } catch (Exception e) {Log.e("What the hell", e.getMessage());}

//                TODO: delete button, dropdown appearance, automatic refresh
            }});

        posted = false;

        scrollParent = findViewById(R.id.scroll_parent);
        final Button displayExistingTradesBtn = findViewById(R.id.display_existing_trades_btn);
        this.displayed = false;
//       TODO: check boolean if_displayed

        // Populate the scrollview on-demand
        displayExistingTradesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayed = true;
                scrollParent.removeAllViews();
//                int i = 0;
//                do {
//                    View child = scrollParent.getChildAt(i);
//                    if (child.getId()==R.id.t2_btn_row){
//                        i += 1;
//                        continue;
//                    }
//                    scrollParent.removeView(child);
//                }
//                while (scrollParent.getChildCount() > 1);

//                for (int i = 0; i < scrollParent.getChildCount(); i++) {
//                    View child = scrollParent.getChildAt(i);
//                    if (child.getId()==R.id.first_row){
//                        continue;
//                    }
//                    scrollParent.removeView(child);
//                }


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
        userDoc = fs.collection("users").document(userID);
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                        // Store the initial values of the user
                                        initialUser = documentSnapshot.toObject(User.class);
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
                                                            MARKETPLACE = new Marketplace(getApplicationContext(), trades);
                                                            tradesLoaded = true;
                                                            populateExistingDeals();
                                                        }
                                                    }
                                                });
                                    }
                                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postTrade(String sellType, String receiveType, int sellQty, int receiveQty) {
        if (userLoaded && tradesLoaded) {
            MARKETPLACE.postTrade(fs, sellType, receiveType, sellQty, receiveQty);
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    public synchronized void acceptTrade(Trade toAccept) {
        if (userLoaded && tradesLoaded) {
            MARKETPLACE.acceptTrade(fs, toAccept.getDocumentID());
        } else {
            Log.d(TAG, "User/trades not yet loaded");
        }
    }

    public void deleteTrade(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected synchronized void populateExistingDeals() {

        int lastRowID = 0;

        if (userLoaded && tradesLoaded) {
            for (int i = 0; i < MARKETPLACE.getTrades().size(); i++) {
                final Trade t = MARKETPLACE.getTrades().get(i);
                final View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
                TextView index = new_row.findViewById(R.id.index2);
                TextView timestamp = new_row.findViewById(R.id.timestamp2);
                TextView username = new_row.findViewById(R.id.username2);
                TextView giving = new_row.findViewById(R.id.giving2);
                TextView receiving = new_row.findViewById(R.id.receiving2);
                final ImageButton t2Btn = new_row.findViewById(R.id.t2_btn2);
                final Button t2Btn_text = new_row.findViewById(R.id.t2_btn_text2);

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
                boolean deleteConfirm = false;
                final boolean finalDeleteConfirm = deleteConfirm;
                t2Btn_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!finalSameUser) {
                            acceptTrade(t);
                            t2Btn_text.setText("Done!");
                        } else {
                            if (!finalDeleteConfirm) {
                                t2Btn_text.setText("Delete?");
                                t2Btn_text.setBackground(getResources().getDrawable(R.drawable.marketplace2_btn2));
                            } else {
                                deleteTrade();
                                t2Btn_text.setText("Deleted!");
                            }

                        }
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


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void addRow2(){
//        //        scroll test
//        ArrayList<String> indexList = new ArrayList<>();
//        ArrayList<String> timestampList = new ArrayList<>();
//        ArrayList<String> usernameList = new ArrayList<>();
//        ArrayList<String> givingList = new ArrayList<>();
//        ArrayList<String> receivingList  = new ArrayList<>();
//
//        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
//        int lastRowID = R.id.first_row;
//
//        for (int i  = 0; i < 20; i++) {
////            creating test strings
//            indexList.add(String.valueOf(i));
//
//            LocalDate newDate = LocalDate.of(2020, Month.APRIL, i+1);
//            String newDateString = newDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
//            timestampList.add(newDateString);
//
//            char c = (char) (i + 64);
//            char[] chars = new char[8];
//            Arrays.fill(chars, c);
//            String newString = new String(chars);
//            usernameList.add(newString);
//
//            givingList.add(String.format("fish x %d", i));
//            receivingList.add(String.format("gold x %d", i));
//
//            View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);
//
////        set content
//            TextView index = (TextView) new_row.findViewById(R.id.index2);
//            TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
//            TextView username = (TextView) new_row.findViewById(R.id.username2);
//            TextView giving = (TextView) new_row.findViewById(R.id.giving2);
//            TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
//            final ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);
//
//            index.setText(indexList.get(i));
//            timestamp.setText(timestampList.get(i));
//            username.setText(usernameList.get(i));
//            giving.setText(givingList.get(i));
//            receiving.setText(receivingList.get(i));
//
//            final Button t2Btn_text = (Button) new_row.findViewById(R.id.t2_btn_text2);
//
//            t2Btn_text.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    acceptDeal();
//                    t2Btn_text.setText("Done!");
//                }
//            });
//
//            t2Btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    t2Btn.setVisibility(View.INVISIBLE);
//                    t2Btn_text.setVisibility(View.VISIBLE);
//                }
//            });
//
//
////        add to parent
//
//            scrollParent.addView(new_row);
//
//
////        set id & then constraints
//            int id = View.generateViewId();
//            Log.d("BRO", String.valueOf(id));
//            new_row.setId(id);
//
////        Remarks: 1. setting the height & weight to equal to first row doesn't work.
////        2. setting the height & weight to the exact dim 312 and 40 also don't work
////        3. setting the dim to 0 also doesn't work
////        4. seem to need to set it super big
////         5. try to have another set for row_set
//
////            ConstraintLayout firstRow = (ConstraintLayout) findViewById(R.id.first_row);
////        int h = firstRow.getHeight();
////        int w = firstRow.getWidth();
////            TextView firstRowTimestamp = (TextView) findViewById(R.id.timestamp33);
//
////        ConstraintSet row_set = new ConstraintSet();
////        row_set.constrainHeight(id, h);
////        row_set.constrainWidth(id, w);
////        row_set.applyTo((ConstraintLayout) new_row);
////            Log.d("AAAAAAA", String.valueOf(firstRowTimestamp.getWidth()));
//
//
//            ConstraintSet set = new ConstraintSet();
//            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
//            set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);
//
//            set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
//            set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
//            set.connect(id, ConstraintSet.TOP, lastRowID, ConstraintSet.BOTTOM, 0);
////            set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//            set.applyTo(scrollParent);
////            Log.d("BBBBBBBB", String.valueOf(timestamp.getWidth()));
//
//            lastRowID = id;
//    }}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
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


