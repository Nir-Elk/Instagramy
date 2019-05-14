package com.instagramy.models;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;

import static android.content.Context.LOCATION_SERVICE;

public class Post implements Serializable {
    private String Key;
    private String title;
    private String description;
    private String picture;
    private String userName;
    private String userImg;
    private int yummies;
    private Double locationLatitude; // X
    private Double locationLongitude; // Y
    private Object timeStamp;



    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

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

    public Post(String title, String description, String picture, String userName, String userImg,Location location) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userName = userName;
        this.userImg = userImg;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.yummies = 0;
        this.locationLatitude=location!=null?location.getLatitude():0;
        this.locationLongitude=location!=null?location.getLongitude():0;
    }

    public Post() { }




}
