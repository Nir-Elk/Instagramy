package com.instagramy.repositories.impl;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.models.Profile;
import com.instagramy.repositories.ProfileRepository;

public class FirebaseProfileRepository implements ProfileRepository {
    private DatabaseReference databaseUsersReference;
    private StorageReference storageUsersPhotosReference;

    public FirebaseProfileRepository(DatabaseReference databaseUsersReference, StorageReference storageUsersPhotosReference) {
        this.databaseUsersReference = databaseUsersReference;
        this.storageUsersPhotosReference = storageUsersPhotosReference;
    }

    @Override
    public DatabaseReference getProfile(String id) {
        return databaseUsersReference.child(id);
    }

    @Override
    public String createNewProfile() {
        return databaseUsersReference.push().getKey();
    }

    @Override
    public Task addProfile(Profile profile) {
        return databaseUsersReference.child(profile.getKey()).setValue(profile);
    }

    @Override
    public UploadTask uploadUserPhoto(String path, Uri photo) {
        return storageUsersPhotosReference.child(path).putFile(photo);
    }

    @Override
    public Task getDownloadUserPhotoUrl(String path) {
        return storageUsersPhotosReference.child(path).getDownloadUrl();
    }

    @Override
    public void changeName(String uid, String name) {
        databaseUsersReference.child(uid).child("name").setValue(name);
    }

    @Override
    public void deleteUser(String key) {
        databaseUsersReference.child(key).removeValue();
    }

}
