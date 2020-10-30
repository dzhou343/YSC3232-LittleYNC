package com.example.littleync;
import com.example.littleync.model.Resource;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MarketplaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//    s-: sell; b-: buy
    Resource sRecourceType;
    private EditText receiveQty;
    private EditText giveQty;
    private Button postDealBtn;
    private Button trade;
    private String logMsg = "Buttery screen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_page2);

//        T1 initialization
//        set up the spinner for the type of resource the user is asking for
        Spinner receiveType = (Spinner) findViewById(R.id.receive_type);
        ArrayAdapter<String> receiveAdapter = new ArrayAdapter<String>(MarketplaceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.marketplace1_spinner));
        receiveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiveType.setAdapter(receiveAdapter);
        receiveType.setOnItemSelectedListener(this);

//        set up the spinner for the type of resource the user is trading with
        Spinner giveType = (Spinner) findViewById(R.id.give_type);
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
                postDeal();
            }
    });

        final String username;
        final Button tradeXML = findViewById(R.id.tradeButton);
        this.trade = tradeXML;
    }

    public void tradePage(View view) {
        Log.d(logMsg, logMsg);
        FirebaseAuth fb = FirebaseAuth.getInstance();
        Log.d(logMsg, fb.getCurrentUser().getUid().toString());}

    protected void postDeal(){
//        TODO: cast the dropdown option to the proper resource type
        sRecourceType = Resource.Fish;

//        TODO: cast the edittext input to int
//        data validation for quantity: 1. Only positive integers are accepted. 2. Cannot > existing qty
        int sQty = 5;
        int sBill = 10;

//        TODO: click the green button -> trigger the posting?


    }

    protected void acceptDeal(){

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}


