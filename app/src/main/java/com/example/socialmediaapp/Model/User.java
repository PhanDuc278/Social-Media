package com.example.socialmediaapp.Model;

public class User {
    String uId , name , img_Profile , description , email , status , calculatorTimeOff;

    public User(){}

    public User(String uId, String name, String img_Profile, String description, String email, String status, String calculatorTimeOff) {
        this.uId = uId;
        this.name = name;
        this.img_Profile = img_Profile;
        this.description = description;
        this.email = email;
        this.status = status;
        this.calculatorTimeOff = calculatorTimeOff;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_Profile() {
        return img_Profile;
    }

    public void setImg_Profile(String img_Profile) {
        this.img_Profile = img_Profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCalculatorTimeOff() {
        return calculatorTimeOff;
    }

    public void setCalculatorTimeOff(String calculatorTimeOff) {
        this.calculatorTimeOff = calculatorTimeOff;
    }
}
