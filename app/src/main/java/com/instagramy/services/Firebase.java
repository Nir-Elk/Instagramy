package com.instagramy.services;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.models.Post;
import com.instagramy.models.Profile;

import java.util.List;

public class Firebase {
    private static final Firebase ourInstance = new Firebase();

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference databaseUsersReference;
    private DatabaseReference databasePostsReference;
    private StorageReference storageUsersPhotosReference;
    private StorageReference storageBlogPhotosReference;

    public static Firebase getInstance() {
        return ourInstance;
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

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void setStorage(FirebaseStorage storage) {
        this.storage = storage;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public void signOut(){
        auth.signOut();
    }

    public Task CreateUserAuth(String email, String pass){
        return auth.createUserWithEmailAndPassword(email,pass);
    }

    public Task signIn(String email, String pass){
        return auth.signInWithEmailAndPassword(email,pass);
    }

    public UploadTask uploadUserPhoto(String path, Uri photo){
        return storageUsersPhotosReference.child(path).putFile(photo);
    }

    public Task getDownloadUserPhotoUrl(String path){
        return storageUsersPhotosReference.child(path).getDownloadUrl();
    }

    public UploadTask uploadPhoto(String path, Uri photo){
        return storageBlogPhotosReference.child(path).putFile(photo);
    }

    public Task getDownloadPhotoUrl(String path){
        return storageBlogPhotosReference.child(path).getDownloadUrl();
    }

    public String createNewProfile(){
        return databaseUsersReference.push().getKey();
    }

    public Task addProfile(Profile profile){
        return databaseUsersReference.setValue(profile);
    }

    public String createNewPost(){
        return databasePostsReference.push().getKey();
    }

    public Task addPost(Post post){
        return databasePostsReference.setValue(post);
    }

    public Task updateUserAuthKey(String key){
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(key)
                .build();
        return auth.getCurrentUser().updateProfile(profileUpdate);
    }

    public DatabaseReference getProfile(String id){
        return databaseUsersReference.child(id);
    }

    public void updateYummies(String key, List<String> list){
        databasePostsReference.child(key).child("yummiesSet").setValue(list);
    }
}
