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
    private String sellType;
    // What resource is requested
    private String receiveType;


    // How much of the resource is being sold
    private int receiveQty;
    // How much of the resource is being requested
    private int sellQty;
    private LocalDateTime timestamp;

    public Trade(String documentID, String userName, String sellType, String receiveType, int sellQty, int receiveQty, LocalDateTime timeOfListing) {
        this.documentID = documentID;
        this.userName = userName;
        this.sellType = sellType;
        this.receiveType = receiveType;
        this.receiveQty = sellQty;
        this.sellQty = receiveQty;
        this.timestamp = timeOfListing;
    }

    // Needed to automatically parse DB
    public Trade() {
    }

    public void writeToDatabase(DocumentReference tradeDoc) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("tradeID", getDocumentID());
        docData.put("selling", getSellType());
        docData.put("receiving", getReceiveType());
        docData.put("quantity", getQuantity());
        docData.put("totalCost", getTotalCost());
        docData.put("timeOfListing", getTimestamp());
        tradeDoc.set(docData);
    }

    public String getUserName() {
        return this.userName;
    }

    public int getReceiveQty() {
        return this.receiveQty;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setReceiveQty(int receiveQty) {
        this.receiveQty = receiveQty;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public void setQuantity(int quantity) {
        this.receiveQty = quantity;
    }

    public void setTotalCost(int totalCost) {
        this.sellQty = totalCost;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentID() {
        return this.documentID;
    }

    public String getSellType() {
        return this.sellType;
    }

    public String getReceiveType() {
        return this.receiveType;
    }

    public int getQuantity() {
        return this.receiveQty;
    }

    public int getSellQty() {
        return this.sellQty;
    }

    public int getTotalCost() {
        return this.sellQty;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}
