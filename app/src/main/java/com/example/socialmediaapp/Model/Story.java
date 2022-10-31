package com.example.socialmediaapp.Model;

import java.util.ArrayList;

public class Story {
    String img_Story , storyId , uId , postTime;
    long timeStart , timeEnd ;
    public Story(){}

    public Story(String img_Story, String storyId, String uId, String postTime, long timeStart, long timeEnd) {
        this.img_Story = img_Story;
        this.storyId = storyId;
        this.uId = uId;
        this.postTime = postTime;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getImg_Story() {
        return img_Story;
    }

    public void setImg_Story(String img_Story) {
        this.img_Story = img_Story;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }
}
