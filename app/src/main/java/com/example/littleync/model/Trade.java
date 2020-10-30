package com.example.littleync.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Trade class
 *
 * Description: Class is generated with each trade activity.
 */
public class Trade {
    private String tradeID;
    // What resource is being sold
    private String selling;
    // What resource is requested
    private String receiving;
    // How much of the resource is being sold
    private int quantity;
    // How much of the resource is being requested
    private int totalCost;

    public Trade(String tradeID, String selling, String receiving, int quantity, int totalCost) {
        this.tradeID = tradeID;
        this.selling = selling;
        this.receiving = receiving;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    // Needed to automatically parse DB
    public Trade() {}

    public void writeToDatabase(OnlineDatabase db) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("tradeID", getTradeID());
        docData.put("selling", getSelling());
        docData.put("receiving", getReceiving());
        docData.put("quantity", getQuantity());
        docData.put("totalCost", getTotalCost());
        db.tradeReadWrite(tradeID).set(docData);
    }

    public String getTradeID() {
        return this.tradeID;
    }

    public String getSelling() {
        return this.selling;
    }

    public String getReceiving() {
        return this.receiving;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getTotalCost() {
        return this.totalCost;
    }

}
