package com.instagramy.helpers.main.activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;

public class MainActivityBottomNavigationHelper {
    private BottomNavigationView bottomNavigationView;

    private MainActivityBottomNavigationHelper() {
    }

    public MainActivityBottomNavigationHelper(MainActivity mainActivity) {
        bottomNavigationView = mainActivity.findViewById(R.id.bottom_navigation);
    }

    public int getSelectedItemBottomNavigation() {
        return bottomNavigationView.getSelectedItemId();
    }

    public void setSelectedItemBottomNavigation(final int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }
}
