package com.gemi.chat_me.Models;

public class Messages {

    String message;
    String senderID;
    String receiverID;

    public Messages() {
    }

    public Messages(String message, String senderID, String receiverID) {
        this.message = message;
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }
}