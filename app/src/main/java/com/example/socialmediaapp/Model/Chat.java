package com.example.socialmediaapp.Model;

public class Chat {
    private String id , senderId ,message , recipient , emoji , isseen;
    private long timeSend ;
    public Chat(){}

    public Chat(String id, String senderId, String message, String recipient, String emoji, String isseen, long timeSend) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.recipient = recipient;
        this.emoji = emoji;
        this.isseen = isseen;
        this.timeSend = timeSend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }
}
