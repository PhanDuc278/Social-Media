package com.example.socialmediaapp.Model;

public class ChatList {
    private String id , lastChat, isSeen;
    public long timeLast ;
    public ChatList(){}

    public ChatList(String id, String lastChat, String isSeen, long timeLast) {
        this.id = id;
        this.lastChat = lastChat;
        this.isSeen = isSeen;
        this.timeLast = timeLast;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public long getTimeLast() {
        return timeLast;
    }

    public void setTimeLast(long timeLast) {
        this.timeLast = timeLast;
    }
}
