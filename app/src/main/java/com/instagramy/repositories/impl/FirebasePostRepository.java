package com.instagramy.repositories.impl;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.models.Post;
import com.instagramy.repositories.PostRepository;

import java.util.List;

public class FirebasePostRepository implements PostRepository {
    private DatabaseReference databasePostsReference;
    private StorageReference storageBlogPhotosReference;

    private FirebasePostRepository() {
    }

    public FirebasePostRepository(DatabaseReference databasePostsReference, StorageReference storageBlogPhotosReference) {
        this.databasePostsReference = databasePostsReference;
        this.storageBlogPhotosReference = storageBlogPhotosReference;
    }

    @Override
    public String createNewPost() {
        return databasePostsReference.push().getKey();
    }

    @Override
    public Task addPost(Post post) {
        return databasePostsReference.child(post.getKey()).setValue(post);
    }

    @Override
    public DatabaseReference getPost(String id) {
        return databasePostsReference.child(id);
    }

    @Override
    public DatabaseReference getPosts() {
        return databasePostsReference;
    }

    @Override
    public void updateYummies(String key, List<String> list) {
        databasePostsReference.child(key).child("yummiesSet").setValue(list);
    }

    @Override
    public Task deletePost(String key) {
        return databasePostsReference.child(key).removeValue();
    }

    @Override
    public UploadTask uploadPhoto(String path, Uri photo) {
        return storageBlogPhotosReference.child(path).putFile(photo);
    }

    @Override
    public Task getDownloadPhotoUrl(String path) {
        return storageBlogPhotosReference.child(path).getDownloadUrl();
    }

}
