package com.instagramy.models;

import android.net.Uri;

import java.io.Serializable;

public class Profile implements Serializable {
    private String name;
    private String Key;
    private String id;
    private String email;
    private Uri imageUri;
    private String aboutMe;

    public Profile() {}

    public Profile(String name, String id, String email, Uri imageUri) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.imageUri = imageUri;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}
