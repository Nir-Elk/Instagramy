package com.instagramy.helpers.main.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.instagramy.R;
import com.instagramy.activities.LoginActivity;
import com.instagramy.activities.MainActivity;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.RepositoryManager;

public class MainActivityMenuHelper {
    private MainActivity mainActivity;
    private AuthRepository authRepository;
    private Menu menu;

    private MainActivityMenuHelper() {
    }

    public MainActivityMenuHelper(MainActivity mainActivity, Menu menu) {
        this.mainActivity = mainActivity;
        this.menu = menu;
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
        this.initBottomBarClickListeners();
    }

    public void onOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post_btn:
                mainActivity.getDialogsHelper().getPopupChooseGalleryOrCamera().show();
                break;

            case R.id.menu_edit_profile_btn:
                navigate(R.id.editProfileFragment);
                break;

            case R.id.menu_refresh_my_favorites:
                navigate(R.id.favoritesFragment);
                break;

            case R.id.menu_logout:
                authRepository.signOut();
                Intent logginActivity = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivity(logginActivity);
                mainActivity.finish();
                break;
            default:
                break;

        }
    }

    public void switchToHomeToolBar() {
        this.inflate(R.menu.toolbar_home);
    }

    private void switchToMyFavoritesToolBar() {
        this.inflate(R.menu.toolbar_refresh_my_favorites);
    }

    private void switchToMyPostsToolBar() {
        this.inflate(R.menu.toolbar_my_posts);
    }

    private void inflate(int menuRes) {
        this.menu.clear();
        this.mainActivity.getMenuInflater().inflate(menuRes, this.menu);
    }


    public void initBottomBarClickListeners() {

        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToHomeToolBar();
                navigate(R.id.action_global_homeFragment);
            }
        });

        findViewById(R.id.nav_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToHomeToolBar();
                navigate(R.id.action_global_mapFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToHomeToolBar();
                navigate(R.id.action_global_settingsFragment);
            }
        });

        findViewById(R.id.nav_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMyFavoritesToolBar();
                navigate(R.id.action_global_favoritesFragment);
            }
        });
        findViewById(R.id.nav_my_posts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMyPostsToolBar();
                navigate(R.id.action_global_myPostsFragment);
            }
        });
    }

    private View findViewById(int viewId) {
        return mainActivity.findViewById(viewId);
    }

    private void navigate(int action) {
        this.mainActivity.getController().navHostFragmentNavigate(action);
    }

}
