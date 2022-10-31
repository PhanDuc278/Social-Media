package com.example.socialmediaapp.Model;

public class Post {
    String id , img_Post , post_Time , description , post_By ;

    public Post(){}

    public Post(String id, String img_Post, String post_Time, String description, String post_By) {
        this.id = id;
        this.img_Post = img_Post;
        this.post_Time = post_Time;
        this.description = description;
        this.post_By = post_By;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_Post() {
        return img_Post;
    }

    public void setImg_Post(String img_Post) {
        this.img_Post = img_Post;
    }

    public String getPost_Time() {
        return post_Time;
    }

    public void setPost_Time(String post_Time) {
        this.post_Time = post_Time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_By() {
        return post_By;
    }

    public void setPost_By(String post_By) {
        this.post_By = post_By;
    }
}
