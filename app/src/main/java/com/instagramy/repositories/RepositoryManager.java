package com.instagramy.repositories;


import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.repositories.impl.FirebaseAuthRepository;
import com.instagramy.repositories.impl.FirebasePostRepository;
import com.instagramy.repositories.impl.FirebaseProfileRepository;
import com.instagramy.repositories.impl.SqlLiteFavoriteRepository;
import com.instagramy.services.FirebaseService;

public class RepositoryManager {
    private static RepositoryManager instance;

    private ProfileRepository profileRepository;
    private PostRepository postRepository;
    private AuthRepository authRepository;

    private RepositoryManager() {
        FirebaseService firebaseService = FirebaseService.getInstance();
        this.profileRepository = new FirebaseProfileRepository(firebaseService.getDatabaseUsersReference(), firebaseService.getStorageUsersPhotosReference());
        this.postRepository = new FirebasePostRepository(firebaseService.getDatabasePostsReference(), firebaseService.getStorageBlogPhotosReference());
        this.authRepository = new FirebaseAuthRepository(firebaseService.getAuth());
    }

    public static RepositoryManager getInstance() {
        if (instance == null) {
            instance = new RepositoryManager();
        }
        return instance;
    }

    public FavoriteRepository getLinkRepository(AppCompatActivity activity) {
        return new SqlLiteFavoriteRepository(activity);
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
}
