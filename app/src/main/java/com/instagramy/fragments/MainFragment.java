package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;

public class MainFragment extends RecycleViewFragment {

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.setTitle("Instagramy");
        ((MainActivity) getActivity()).getBottomNavigationHelper().setSelectedItemBottomNavigation(R.id.nav_home);
    }
}
