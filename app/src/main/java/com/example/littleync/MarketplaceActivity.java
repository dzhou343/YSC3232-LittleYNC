package com.example.littleync;
import com.example.littleync.model.Marketplace;
import com.example.littleync.model.Resource;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.littleync.model.Trade;
import com.example.littleync.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//    s-: sell; b-: buy
    Resource sRecourceType;
    private EditText receiveQty;
    private EditText giveQty;
    private Button postDealBtn;
    private Button trade;
    private String logMsg = "Buttery screen";
    private Marketplace m;
    private String userID;
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
        userID = FirebaseAuth.getInstance().getUid();
        m = new Marketplace(0);
    }

    public void tradePage(View view) {
        Log.d(logMsg, logMsg);
        FirebaseAuth fb = FirebaseAuth.getInstance();
        Log.d(logMsg, fb.getCurrentUser().getUid().toString());}




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

    protected void acceptDeal(Trade t){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void populateExistingDeals(ArrayList<Trade> existingDeals){
        //        scroll test

        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
        int lastRowID = R.id.first_row;

        for (int i = 0; i < existingDeals.size(); i++){
            Trade t = existingDeals.get(i);

            View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
            TextView index = (TextView) new_row.findViewById(R.id.index2);
            TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
            TextView username = (TextView) new_row.findViewById(R.id.username2);
            TextView giving = (TextView) new_row.findViewById(R.id.giving2);
            TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
            ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);

            index.setText(String.valueOf(i + 1));
            timestamp.setText(t.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            username.setText(t.getUserName());
            giving.setText(String.format(Locale.getDefault(),"%s x %d", t.getSellType(), t.getSellQty()));
            receiving.setText(String.format(Locale.getDefault(),"%s x %d", t.getReceiveType(), t.getReceiveQty()));

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

    }}

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void populateExistingDeals2(ArrayList<Trade> existingDeals){
        //        scroll test

        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
        int lastRowID = R.id.first_row;

        for (int i = 0; i < existingDeals.size(); i++){
            final Trade t = existingDeals.get(i);

            View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
            TextView index = (TextView) new_row.findViewById(R.id.index2);
            TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
            TextView username = (TextView) new_row.findViewById(R.id.username2);
            TextView giving = (TextView) new_row.findViewById(R.id.giving2);
            TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
            final ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);

            index.setText(String.valueOf(i + 1));
            timestamp.setText(t.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            username.setText(t.getUserName());
            giving.setText(String.format(Locale.getDefault(),"%s x %d", t.getSellType(), t.getSellQty()));
            receiving.setText(String.format(Locale.getDefault(),"%s x %d", t.getReceiveType(), t.getReceiveQty()));

            clicked = false;
            finished = false;

//          set accept button
            t2Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clicked) {
                        acceptDeal(t);
                        clicked = false;
                        }
                    else {
                        Log.d("Haha", "Else");
                    }
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

        }}

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        TextView firstRowTimestamp = (TextView) findViewById(R.id.timestamp33);

//        ConstraintSet row_set = new ConstraintSet();
//        row_set.constrainHeight(id, h);
//        row_set.constrainWidth(id, w);
//        row_set.applyTo((ConstraintLayout) new_row);
        Log.d("AAAAAAA", String.valueOf(firstRowTimestamp.getWidth()));


        ConstraintSet set = new ConstraintSet();
        set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);

        set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        set.connect(id, ConstraintSet.TOP, R.id.first_row, ConstraintSet.BOTTOM, 0);
        set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyTo(scrollParent);
        Log.d("BBBBBBBB", String.valueOf(timestamp.getWidth()));



        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addRow2(){
        //        scroll test
        ArrayList<String> indexList = new ArrayList<>();
        ArrayList<String> timestampList = new ArrayList<>();
        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<String> givingList = new ArrayList<>();
        ArrayList<String> receivingList  = new ArrayList<>();

        ConstraintLayout scrollParent = findViewById(R.id.scroll_box);
        int lastRowID = R.id.first_row;

        for (int i  = 0; i < 20; i++) {
//            creating test strings
            indexList.add(String.valueOf(i));

            LocalDate newDate = LocalDate.of(2020, Month.APRIL, i+1);
            String newDateString = newDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            timestampList.add(newDateString);

            char c = (char) (i + 64);
            char[] chars = new char[8];
            Arrays.fill(chars, c);
            String newString = new String(chars);
            usernameList.add(newString);

            givingList.add(String.format("fish x %d", i));
            receivingList.add(String.format("gold x %d", i));

            View new_row = getLayoutInflater().inflate(R.layout.t2_row, null, false);

//        set content
            TextView index = (TextView) new_row.findViewById(R.id.index2);
            TextView timestamp = (TextView) new_row.findViewById(R.id.timestamp2);
            TextView username = (TextView) new_row.findViewById(R.id.username2);
            TextView giving = (TextView) new_row.findViewById(R.id.giving2);
            TextView receiving = (TextView) new_row.findViewById(R.id.receiving2);
            ImageButton t2Btn = (ImageButton) new_row.findViewById(R.id.t2_btn2);

            index.setText(indexList.get(i));
            timestamp.setText(timestampList.get(i));
            username.setText(usernameList.get(i));
            giving.setText(givingList.get(i));
            receiving.setText(receivingList.get(i));

//        add to parent

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

//            ConstraintLayout firstRow = (ConstraintLayout) findViewById(R.id.first_row);
//        int h = firstRow.getHeight();
//        int w = firstRow.getWidth();
//            TextView firstRowTimestamp = (TextView) findViewById(R.id.timestamp33);

//        ConstraintSet row_set = new ConstraintSet();
//        row_set.constrainHeight(id, h);
//        row_set.constrainWidth(id, w);
//        row_set.applyTo((ConstraintLayout) new_row);
//            Log.d("AAAAAAA", String.valueOf(firstRowTimestamp.getWidth()));


            ConstraintSet set = new ConstraintSet();
            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(id, ConstraintSet.WRAP_CONTENT);

            set.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            set.connect(id, ConstraintSet.TOP, lastRowID, ConstraintSet.BOTTOM, 0);
//            set.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            set.applyTo(scrollParent);
//            Log.d("BBBBBBBB", String.valueOf(timestamp.getWidth()));

            lastRowID = id;
    }}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}


