package com.instagramy.helpers.main.activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;

public class MainActivityBottomNavigationHelper {
    private BottomNavigationView bottomNavigationView;
    private MainActivity mainActivity;

    private MainActivityBottomNavigationHelper() {

    }

    public MainActivityBottomNavigationHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        bottomNavigationView = mainActivity.findViewById(R.id.bottom_navigation);

    }

    public void setSelectedItemBottomNavigation(final int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);

    }
}
