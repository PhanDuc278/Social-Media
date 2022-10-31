package com.example.socialmediaapp.Model;

public class Comment {
    String id , comment , publisher , timeCmt ;
    public Comment(){}

    public Comment(String id, String comment, String publisher, String timeCmt) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.timeCmt = timeCmt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTimeCmt() {
        return timeCmt;
    }

    public void setTimeCmt(String timeCmt) {
        this.timeCmt = timeCmt;
    }
}
