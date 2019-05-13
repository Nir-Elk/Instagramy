package com.instagramy.models;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class Post implements Serializable {
    private String Key;
    private String title;
    private String description;
    private String picture;
    private String userName;
    private String userImg;
    private int yummies;

    public int getYummies() {
        return yummies;
    }

    public void setYummies(int yummies) {
        this.yummies = yummies;
    }


    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    private Object timeStamp;

    public Post(String title, String description, String picture, String userName, String userImg) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userName = userName;
        this.userImg = userImg;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.yummies = 0;

    }

    public Post() { }




}
