package com.instagramy.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Link implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "post_img")
    private String image;


    public Link() {
    }


    public Link(int id, String image) {
        this.id = id;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
