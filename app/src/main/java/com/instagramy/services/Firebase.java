package com.instagramy.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    private static Firebase instance;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference databaseUsersReference;
    private DatabaseReference databasePostsReference;
    private StorageReference storageUsersPhotosReference;
    private StorageReference storageBlogPhotosReference;


    public static Firebase getInstance() {
        if (instance == null) {
            instance = new Firebase();
        }
        return instance;
    }

    private Firebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        databaseUsersReference = database.getReference("Profiles");
        databasePostsReference = database.getReference("Posts");
        storageUsersPhotosReference = storage.getReference().child("users_photos");
        storageBlogPhotosReference = storage.getReference().child("users_photos");
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabaseUsersReference() {
        return databaseUsersReference;
    }

    public DatabaseReference getDatabasePostsReference() {
        return databasePostsReference;
    }

    public StorageReference getStorageUsersPhotosReference() {
        return storageUsersPhotosReference;
    }

    public StorageReference getStorageBlogPhotosReference() {
        return storageBlogPhotosReference;
    }
}
