package com.example.socialmediaapp.Model;

public class Notification {
    private String   noId , uId , id ,text , isPost , time;

    public Notification(){}

    public Notification(String noId, String uId, String id, String text, String isPost, String time) {
        this.noId = noId;
        this.uId = uId;
        this.id = id;
        this.text = text;
        this.isPost = isPost;
        this.time = time;
    }

    public String getNoId() {
        return noId;
    }

    public void setNoId(String noId) {
        this.noId = noId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
