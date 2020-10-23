package com.example.littleync.model;

import java.util.Map;

////////////////////////////////////////
// NOTE THIS ENTIRE CLASS IS UNTESTED //
////////////////////////////////////////
public class Marketplace {
    private Map<String, Trade> trades;
    private OnlineDatabase db;

    public Marketplace(OnlineDatabase db) {
        this.db = db;
        this.trades = db.readAllTrades();
    }

    public Boolean postTrade(User user, String selling, String receiving, int quantity, int totalCost) {
        int liveTrades = user.getTrades().size();
        if (liveTrades < 5) {
            String newTradeID = user.getUserName() + liveTrades;
            Trade newTrade = new Trade(newTradeID, selling, receiving, quantity, totalCost);
            switch (selling) {
                case "wood":
                    if (user.getWood() <= quantity) {
                        // Deposit the resource the user wants to trade
                        user.setWood(user.getWood() - quantity);
                    } else {
                        // The user does not have enough to deposit
                        return false;
                    }
                    break;
                case "fish":
                    if (user.getFish() <= quantity) {
                        user.setFish(user.getFish() - quantity);
                    } else {
                        return false;
                    }
                    break;
                default:
                    if (user.getGold() <= quantity) {
                        user.setGold(user.getGold() - quantity);
                    } else {
                        return false;
                    }
                    break;
            }
            // If the user has enough of the resource to deposit, then we can
            // proceed with physically processing the trade
            // Write trade to DB
            newTrade.writeToDatabase(db);
            // Add the trade to the user's live trades
            user.addTrade(newTradeID);
            // Return true to display that the trade was successfully posted
            return true;
        } else {
            // The user already has 5 live trades, which is the max
            // Return false to display that the trade was unsuccessful
            return false;
        }
    }

    public void listLiveTrades() {
        for (Trade t : trades.values()) {
            System.out.println(t.getTradeID());
        }
    }

    public void listUserTrades(User user) {
        for (String tradeID : user.getTrades()) {
            System.out.println(trades.get(tradeID));
        }
    }

    public Boolean acceptTrade(User user, String tradeID) {
        Trade acceptingTrade = trades.get(tradeID);
        String otherUser = tradeID.substring(0, tradeID.length() - 1);
        switch (acceptingTrade.getReceiving()) {
            case "wood":
                if (user.getWood() >= acceptingTrade.getTotalCost()) {
                    // Debit the resource of the accepted user
                    // TODO
                    // Less off the resource from the accepting user
                    user.setWood(user.getWood() - acceptingTrade.getTotalCost());
                    // Debit the resource received
                    if (acceptingTrade.getSelling().equals("wood")) {
                        user.setWood(user.getWood() + acceptingTrade.getQuantity());
                    } else if (acceptingTrade.getSelling().equals("fish")) {
                        user.setFish(user.getFish() + acceptingTrade.getQuantity());
                    } else {
                        user.setGold(user.getGold() + acceptingTrade.getQuantity());
                    }
                } else {
                    // Accepting user does not have enough resources to trade
                    return false;
                }
                break;
            case "fish":
                if (user.getFish() >= acceptingTrade.getTotalCost()) {
                    // Debit the resource of the accepted user
                    // TODO
                    // Less off the resource from the accepting user
                    user.setFish(user.getFish() - acceptingTrade.getTotalCost());
                    // Debit the resource received
                    if (acceptingTrade.getSelling().equals("wood")) {
                        user.setWood(user.getWood() + acceptingTrade.getQuantity());
                    } else if (acceptingTrade.getSelling().equals("fish")) {
                        user.setFish(user.getFish() + acceptingTrade.getQuantity());
                    } else {
                        user.setGold(user.getGold() + acceptingTrade.getQuantity());
                    }
                } else {
                    return false;
                }
                break;
            default:
                if (user.getGold() >= acceptingTrade.getTotalCost()) {
                    // Debit the resource of the accepted user
                    // TODO
                    // Less off the resource from the accepting user
                    user.setGold(user.getGold() - acceptingTrade.getTotalCost());
                    // Debit the resource received
                    if (acceptingTrade.getSelling().equals("wood")) {
                        user.setWood(user.getWood() + acceptingTrade.getQuantity());
                    } else if (acceptingTrade.getSelling().equals("fish")) {
                        user.setFish(user.getFish() + acceptingTrade.getQuantity());
                    } else {
                        user.setGold(user.getGold() + acceptingTrade.getQuantity());
                    }
                } else {
                    return false;
                }
                break;
        }
        // Trade is completed
        return true;
    }

}
