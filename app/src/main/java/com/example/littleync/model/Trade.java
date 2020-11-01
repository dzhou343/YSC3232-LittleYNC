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
    private int sellQty;
    // How much of the resource is being requested
    private int receiveQty;
    private String timeOfListing;

    public Trade(String documentID, String userName, String sellType, String receiveType, int sellQty, int receiveQty, String timeOfListing) {
        this.documentID = documentID;
        this.userName = userName;
        this.sellType = sellType;
        this.receiveType = receiveType;
        this.sellQty = sellQty;
        this.receiveQty = receiveQty;
        this.timeOfListing = timeOfListing;
    }

    // Needed to automatically parse DB
    public Trade() {}

    public void writeToDatabase(DocumentReference tradeDoc) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("documentID", getDocumentID());
        docData.put("userName", getUserName());
        docData.put("sellType", getSellType());
        docData.put("receiveType", getReceiveType());
        docData.put("sellQty", getSellQty());
        docData.put("receiveQty", getReceiveQty());
        docData.put("timeOfListing", getTimeOfListing());
        tradeDoc.set(docData);
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public int getSellQty() {
        return sellQty;
    }

    public void setSellQty(int sellQty) {
        this.sellQty = sellQty;
    }

    public int getReceiveQty() {
        return receiveQty;
    }

    public void setReceiveQty(int receiveQty) {
        this.receiveQty = receiveQty;
    }

    public String getTimeOfListing() {
        return timeOfListing;
    }

    public void setTimeOfListing(String timeOfListing) {
        this.timeOfListing = timeOfListing;
    }

}
