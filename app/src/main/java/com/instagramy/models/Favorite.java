package com.instagramy.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Favorite implements Serializable {

    @NonNull
    @PrimaryKey
    private String postId;

    public Favorite() {
    }

    public Favorite(String postId) {
        this.postId = postId;
    }

    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

}
