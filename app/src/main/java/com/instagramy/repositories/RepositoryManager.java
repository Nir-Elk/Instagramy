package com.instagramy.repositories;


import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.repositories.impl.FirebaseAuthRepository;
import com.instagramy.repositories.impl.FirebasePostRepository;
import com.instagramy.repositories.impl.FirebaseProfileRepository;
import com.instagramy.repositories.impl.InternalStorageRepository;
import com.instagramy.repositories.impl.SqlLiteLinkRepository;
import com.instagramy.services.Firebase;

public class RepositoryManager {
    private static RepositoryManager instance;

    private ProfileRepository profileRepository;
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private StorageRepository storageRepository;

    private RepositoryManager() {
        Firebase firebase = Firebase.getInstance();
        this.profileRepository = new FirebaseProfileRepository(firebase.getDatabaseUsersReference(), firebase.getStorageUsersPhotosReference());
        this.postRepository = new FirebasePostRepository(firebase.getDatabasePostsReference(), firebase.getStorageBlogPhotosReference());
        this.authRepository = new FirebaseAuthRepository(firebase.getAuth());
        this.storageRepository = new InternalStorageRepository();
    }

    public static RepositoryManager getInstance() {
        if (instance == null) {
            instance = new RepositoryManager();
        }
        return instance;
    }

    public LinkRepository getLinkRepository(AppCompatActivity activity) {
        return new SqlLiteLinkRepository(activity);
    }

    public ProfileRepository getProfileRepository() {
        return profileRepository;
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public AuthRepository getAuthRepository() {
        return this.authRepository;
    }

    public StorageRepository getStorageRepository() {
        return this.storageRepository;
    }



}
