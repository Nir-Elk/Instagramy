package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.repositories.RepositoryManager;


public class MyPostsFragment extends RecycleViewFragment {

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    boolean filter(Post post) {
        return post.getUserId().equals(RepositoryManager.getInstance().getAuthRepository().getCurrentUser().getDisplayName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.setTitle("My Posts");
        ((MainActivity) getActivity()).setSelectedItemBottomNavigation(R.id.nav_my_posts);
    }
}
