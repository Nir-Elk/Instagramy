package com.instagramy.repositories.impl;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.instagramy.repositories.AuthRepository;

public class FirebaseAuthRepository implements AuthRepository {
    private FirebaseAuth auth;

    private FirebaseAuthRepository() {
    }

    public FirebaseAuthRepository(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    @Override
    public void signOut() {
        auth.signOut();
    }

    @Override
    public Task delete() {
        return auth.getCurrentUser().delete();
    }

    @Override
    public Task createUserAuth(String email, String pass) {
        return auth.createUserWithEmailAndPassword(email, pass);
    }

    @Override
    public Task updateUserAuthKey(String key) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(key)
                .build();
        return auth.getCurrentUser().updateProfile(profileUpdate);
    }

    @Override
    public Task signIn(String email, String pass) {
        return auth.signInWithEmailAndPassword(email, pass);
    }

    @Override
    public void changePass(String pass) {
        getCurrentUser().updatePassword(pass);
    }
}
