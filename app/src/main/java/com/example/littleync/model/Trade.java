package com.example.littleync.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Trade object with attributes that match what is stored in the trades collection of the DB
 */
public class Trade {
    private String documentID;
    private String userName;
    private String sellType;
    private String receiveType;
    private int sellQty;
    private int receiveQty;
    private String timeOfListing;

    /**
     * Constructor for the Trade object, this is called when posting a new trade
     *
     * @param documentID    that corresponds to the documentID of this trade in  the trades collection
     *                      of the DB
     * @param userName      the userName of the User that is selling (i.e. the User that posts the
     *                      trade); we need to maintain this in the DB so that we can update the
     *                      seller's attributes when their trade gets accepted
     * @param sellType      the resource being sold, can be "wood", "fish", or "gold"
     * @param receiveType   the resource requested, can be "wood", "fish", or "gold"
     * @param sellQty       the amount of resource being sold, must be >= 0
     * @param receiveQty    the amount of resource requested, must be >= 0
     * @param timeOfListing the String representation of the time of posting, serialized from
     *                      LocalDateTime object
     */
    public Trade(String documentID, String userName, String sellType, String receiveType, int sellQty, int receiveQty, String timeOfListing) {
        this.documentID = documentID;
        this.userName = userName;
        this.sellType = sellType;
        this.receiveType = receiveType;
        this.sellQty = sellQty;
        this.receiveQty = receiveQty;
        this.timeOfListing = timeOfListing;
    }

    /**
     * Empty constructor required to automatically parse Trade document from DB
     */
    public Trade() {}

    /**
     * Write the selected Trade object to DB
     *
     * @param tradeDoc the DocumentReference that points to the Trade of interest
     */
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

    /////////////////////////////////////////////////////////////////////////////////////
    // From here onwards are getters and setters; note that these are all required for //
    // Firestore API to automatically parse from DB into a local Trade object          //
    /////////////////////////////////////////////////////////////////////////////////////

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
