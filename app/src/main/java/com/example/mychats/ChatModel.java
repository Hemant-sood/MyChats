package com.example.mychats;

public class ChatModel {

    private String ReceiverId, SenderID, messageText;
    private long Timestamp;

    public ChatModel() {

    }

    public ChatModel(String receiverId, String senderId, String messageText, long timestamp) {
        ReceiverId = receiverId;
        SenderID = senderId;
        this.messageText = messageText;
        Timestamp = timestamp;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getSenderId() {
        return SenderID;
    }

    public void setSenderId(String senderId) {
        SenderID = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }
}
