package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;

public class ActionBarFragment extends Fragment {

    ActionBar actionBar;
    MainActivity mainActivity;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = ((MainActivity) getActivity());
        this.actionBar = mainActivity.getSupportActionBar();
        mainActivity.setSelectedItemBottomNavigation(R.id.nav_settings);
    }

    public void setTitle(String title) {
        actionBar.setTitle(title);
    }

    public void setTitle(int stringId) {
        actionBar.setTitle(stringId);
    }

    public void hideActionBar() {
        actionBar.hide();
    }

    public void showActionBar() {
        actionBar.show();
    }

    public void setSelectedItem(int itemId) {
        mainActivity.setSelectedItemBottomNavigation(itemId);
    }
}