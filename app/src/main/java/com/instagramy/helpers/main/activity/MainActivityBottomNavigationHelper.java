package com.instagramy.helpers.main.activity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;

public class MainActivityBottomNavigationHelper extends MainActivityHelper {
    private BottomNavigationView bottomNavigationView;
    private MainActivityMenuHelper menuHelper;

    public MainActivityBottomNavigationHelper(MainActivity mainActivity) {
        super(mainActivity);
        bottomNavigationView = mainActivity.findViewById(R.id.bottom_navigation);
        menuHelper = mainActivity.getMenuHelper();
    }

    public int getSelectedItemBottomNavigation() {
        return bottomNavigationView.getSelectedItemId();
    }

    public void setSelectedItemBottomNavigation(final int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

}
