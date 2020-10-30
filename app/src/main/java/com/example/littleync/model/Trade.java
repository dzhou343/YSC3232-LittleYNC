package com.example.littleync.model;

import com.google.firebase.firestore.DocumentReference;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Trade class
 * <p>
 * Description: Class is generated with each trade activity.
 */
public class Trade {
    private String documentID;
    private String userName;
    // What resource is being sold
    private String selling;
    // What resource is requested
    private String receiving;


    // How much of the resource is being sold
    private int quantityRequested;
    // How much of the resource is being requested
    private int totalCost;
    private LocalDateTime timeOfListing;

    public Trade(String documentID, String userName, String selling, String receiving, int quantity, int totalCost, LocalDateTime timeOfListing) {
        this.documentID = documentID;
        this.userName = userName;
        this.selling = selling;
        this.receiving = receiving;
        this.quantityRequested = quantity;
        this.totalCost = totalCost;
        this.timeOfListing = timeOfListing;
    }

    // Needed to automatically parse DB
    public Trade() {
    }

    public void writeToDatabase(DocumentReference tradeDoc) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("tradeID", getDocumentID());
        docData.put("selling", getSelling());
        docData.put("receiving", getReceiving());
        docData.put("quantity", getQuantity());
        docData.put("totalCost", getTotalCost());
        docData.put("timeOfListing", getTimeOfListing());
        tradeDoc.set(docData);
    }

    public String getUserName() {
        return this.userName;
    }

    public int getQuantityRequested() {
        return this.quantityRequested;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public void setSelling(String selling) {
        this.selling = selling;
    }

    public void setReceiving(String receiving) {
        this.receiving = receiving;
    }

    public void setQuantity(int quantity) {
        this.quantityRequested = quantity;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setTimeOfListing(LocalDateTime timeOfListing) {
        this.timeOfListing = timeOfListing;
    }

    public String getDocumentID() {
        return this.documentID;
    }

    public String getSelling() {
        return this.selling;
    }

    public String getReceiving() {
        return this.receiving;
    }

    public int getQuantity() {
        return this.quantityRequested;
    }

    public int getTotalCost() {
        return this.totalCost;
    }

    public LocalDateTime getTimeOfListing() {
        return this.timeOfListing;
    }
}
