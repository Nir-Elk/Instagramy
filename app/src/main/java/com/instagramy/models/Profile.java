package com.instagramy.models;

import java.io.Serializable;

public class Profile implements Serializable {
    private String name;
    private String key;
    private String id;
    private String email;
    private String imageUri;
    private String aboutMe;

    public Profile() {
    }

    public Profile(String name, String id, String email, String imageUri) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.imageUri = imageUri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

}
