package com.instagramy.models;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity(tableName = "<posts>")
public class Post implements Serializable {
    @PrimaryKey
    private String Key;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "picture")
    private String picture;
    private String userId;
    private String userName;
    private String userimg;
    @ColumnInfo(name = "locationLatitude")
    private Double locationLatitude; // X
    @ColumnInfo(name = "locationLongitude")
    private Double locationLongitude; // Y
    private Object timeStamp;
    private List<String> yummiesSet;

    public List<String> getYummiesSet() {
        return this.yummiesSet;
    }

    public void setYummiesSet(List<String> yummiesSet) {
        this.yummiesSet = yummiesSet;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public List<String> toggleYummi(String email) {
        if (this.yummiesSet.contains(email)) {
            this.yummiesSet.remove(email);
        } else {
            this.yummiesSet.add(email);
        }
        return this.yummiesSet;
    }

    public boolean alreadyYummi(String email) {
        return this.yummiesSet.contains(email);
    }

    public int getYummies() {
        return this.yummiesSet.size() - 1;
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

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Post(String title, String description, String picture, String userId, String userName, String userimg, Location location) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userName = userName;
        this.userimg = userimg;
        this.locationLatitude = location != null ? location.getLatitude() : 0;
        this.locationLongitude = location != null ? location.getLongitude() : 0;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.yummiesSet = new LinkedList<>();
        this.yummiesSet.add("a");
    }

    public Post() {
    }


}
