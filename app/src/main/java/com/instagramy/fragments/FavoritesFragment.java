package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;

public class FavoritesFragment extends RecycleViewFragment {


    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    boolean filter(Post post) {
        return favorites.contains(post.getKey());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.setTitle("My Manches!");
        ((MainActivity) getActivity()).setSelectedItemBottomNavigation(R.id.nav_favorites);
    }

}
