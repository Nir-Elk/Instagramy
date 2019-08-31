package com.instagramy.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Link implements Serializable {

    @NonNull
    @PrimaryKey
    private String postId;

    @ColumnInfo(name = "post_img")
    private String image;


    public Link(String postId, String image) {
        this.postId = postId;
        this.image = image;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
