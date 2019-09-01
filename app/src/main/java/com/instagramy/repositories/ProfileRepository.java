package com.instagramy.repositories;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.models.Profile;

public interface ProfileRepository {

    DatabaseReference getProfile(String id);

    String createNewProfile();

    Task addProfile(Profile profile);

    UploadTask uploadUserPhoto(String path, Uri photo);

    Task getDownloadUserPhotoUrl(String path);

    void changeName(String uid, String name);

    void deleteUser(String key);
}
