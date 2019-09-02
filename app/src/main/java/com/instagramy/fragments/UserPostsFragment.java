package com.instagramy.fragments;

import android.os.Bundle;

import com.instagramy.models.Post;


public class UserPostsFragment extends RecycleViewFragment {
    String profileId;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.profileId = UserPostsFragmentArgs.fromBundle(getArguments()).getProfileId();

    }

    @Override
    boolean filter(Post post) {
        return post.getUserId().equals(profileId);
    }

}
