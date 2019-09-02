package com.instagramy.helpers.main.activity;

import android.view.View;

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

    private void initBottomBarClickListeners() {

        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToHomeToolBar();
                navigate(R.id.action_global_homeFragment);
            }
        });

        findViewById(R.id.nav_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToHomeToolBar();
                navigate(R.id.action_global_mapFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToSettingsToolBar();
                navigate(R.id.action_global_settingsFragment);
            }
        });

        findViewById(R.id.nav_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToMyFavoritesToolBar();
                navigate(R.id.action_global_favoritesFragment);
            }
        });
        findViewById(R.id.nav_my_posts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToMyPostsToolBar();
                navigate(R.id.action_global_myPostsFragment);
            }
        });
    }

}
