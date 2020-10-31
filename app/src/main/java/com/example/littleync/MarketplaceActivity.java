package com.example.littleync;
import com.example.littleync.model.Marketplace;
import com.example.littleync.model.Resource;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.littleync.model.Trade;
import com.example.littleync.model.User;
import com.example.littleync.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // To print to log instead of console
    private final static String TAG = "MarketplaceActivity";

    // DB attributes
    private final FirebaseFirestore fs = FirebaseFirestore.getInstance();
    private DocumentReference userDoc;
    private User user;
    private User initialUser;
    private volatile Boolean userLoaded = false;

//    s-: sell; b-: buy
    Resource sRecourceType;
    private EditText receiveQty;
    private EditText giveQty;
    private Button postDealBtn;
    private Button trade;
    private Marketplace m;


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
        postDealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posted) {
                    postDealBtn.setBackgroundColor(Color.TRANSPARENT);
                    postDealBtn.setText("Posting");
                    postDealBtn.setTextColor(getApplication().getResources().getColor(R.color.marketplace1_btn));
                    posted = false;
                } else {
                    try {int s = Integer.parseInt(receiveQty.toString());} catch (Exception e) {Log.e("What the hell", e.getMessage());}
                    postDeal(receiveType.toString(), Integer.parseInt(receiveQty.toString()), giveType.toString(), Integer.parseInt(giveQty.toString()));}
            }
    });
        posted = false;

//        T2 initialization
        ArrayList<Trade> existingDeals = new ArrayList<Trade>();
        Trade t1 = new Trade("d1", "bro", "Gold", "Fish", 5, 10, LocalDateTime.now());
        Trade t2 = new Trade("sss5", "sis", "Wood", "Fish", 77, 1, LocalDateTime.now());
        Trade t3 = new Trade("OOOOO", "dude", "Wood", "Gold", 100, 99, LocalDateTime.now());

        existingDeals.add(t1);
        existingDeals.add(t2);
        existingDeals.add(t3);
        populateExistingDeals(existingDeals);

//      Yun Da & Mark
        final String username;
        final Button tradeXML = findViewById(R.id.tradeButton);
        this.trade = tradeXML;

        String userID = FirebaseAuth.getInstance().getUid();
        userLoaded = false;
        userDoc = fs.collection("users").document(userID);
        readUser(userDoc.get());
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

    public void tradePage(View view) {
        Log.d(TAG, TAG);
        FirebaseAuth fb = FirebaseAuth.getInstance();
        Log.d(TAG, fb.getCurrentUser().getUid().toString());}




    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void postDeal(String receiveTypeStr, int receiveQtyInt, String giveTypeStr, int giveQtyInt){
//        data validation for quantity: 1. Only positive integers are accepted. 2. Cannot > existing qty
        User u = new User();
        boolean validDeal = m.postTrade(u, giveTypeStr, receiveTypeStr, giveQtyInt, receiveQtyInt);

//        if (receiveTypeStr.equals(giveTypeStr)){
//            giveQty.setError("The resource you are trading for cannot be of the same type as that of the resource you are trading with.");
//            successfulPosting = false;
//        }
//        if (receiveQtyInt < 0){
//            receiveQty.setError("You cannot trade for non-positve units of resources.");
//            successfulPosting = false;
//        }
//        if (giveQtyInt < 0){
//            giveQty.setError("You cannot trade with non-positive units of resources");
//            successfulPosting = false;
//        }
//
//        if m.postTrade

        if (validDeal) {
//            TODO: print out a success msg in the button
            postDealBtn.setText("Successfully posted! Post another deal?");
            postDealBtn.setBackgroundResource(R.drawable.marketplace2_btn);
            postDealBtn.setTextColor(0xff0000);
            posted = true;
        } else {
            giveQty.setError("You have input an invalid quantity for trading. Please try again.");
        }

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

    }

    protected void acceptDeal(){

    }

    protected void populateExistingDeals(ArrayList<Trade> existingDeals){

//        can keep a last row id reference here?
//        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
//        for (int i = 0; i < existingDeals.size(); i++){
//            Trade t = existingDeals.get(i);
//
//            ConstraintLayout this_row = new ConstraintLayout(this);
//            ConstraintLayout.LayoutParams this_param = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            this_param.setMargins(8,0,0,0);
//            this_row.setLayoutParams(this_param);
////            TODO: need to set constraints
//
//            t.getTimestamp().toString();
//        }

// creation of a new constraintlayout for a new row
//        ConstraintLayout this_row = new ConstraintLayout(this);

//        int id = View.generateViewId();
//        Log.d("BRO", String.valueOf(id));
//        this_row.setId(id);
//
//        scrollParent.addView(this_row);
//
//        ConstraintSet set = new ConstraintSet();
//        set.constrainHeight(id, 40);
//        set.constrainWidth(id, 100);
//        set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
//        set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
//        set.connect(id, ConstraintSet.TOP, R.id.first_row, ConstraintSet.BOTTOM, 0);
//        set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
//        set.applyTo(scrollParent);

        addRow();



    }

    @SuppressLint("SetTextI18n")
    private void addRow() {
        // creation of a new constraintlayout for a new row 2

        final View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
        TextView index = (TextView) new_row.findViewById(R.id.index2);
        TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
        TextView username = (TextView) new_row.findViewById(R.id.username2);
        TextView giving = (TextView) new_row.findViewById(R.id.giving2);
        TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
        ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);

        index.setText("2");
        timestamp.setText("20th Oct 2020");
        username.setText("DDDDD");
        giving.setText("gold x 50");
        receiving.setText("wood x 1");

//        find parent and add to parent
        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
        scrollParent.addView(new_row);


//        set id & then constraints
        int id = View.generateViewId();
        Log.d("BRO", String.valueOf(id));
        new_row.setId(id);

//        Remarks: 1. setting the height & weight to equal to first row doesn't work.
//        2. setting the height & weight to the exact dim 312 and 40 also don't work
//        3. setting the dim to 0 also doesn't work
//        4. seem to need to set it super big
//         5. try to have another set for row_set

        ConstraintLayout firstRow = (ConstraintLayout) findViewById(R.id.first_row);
//        int h = firstRow.getHeight();
//        int w = firstRow.getWidth();

//        ConstraintSet row_set = new ConstraintSet();
//        row_set.constrainHeight(id, h);
//        row_set.constrainWidth(id, w);
//        row_set.applyTo((ConstraintLayout) new_row);
        Log.d("AAAAAAA", "Hhhhhhhhhh");


        ConstraintSet set = new ConstraintSet();
        set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);

        set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        set.connect(id, ConstraintSet.TOP, R.id.first_row, ConstraintSet.BOTTOM, 0);
        set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyTo(scrollParent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}


