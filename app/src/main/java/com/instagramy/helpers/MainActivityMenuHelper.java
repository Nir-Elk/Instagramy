package com.instagramy.helpers;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

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
    }

    public void onOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_post_btn:
                mainActivity.getPopupChooseGalleryOrCamera().show();
                break;

            case R.id.menu_edit_profile_btn:
                mainActivity.navHostFragmentNavigate(R.id.editProfileFragment);
                break;

            case R.id.menu_refresh_my_favorites:
                mainActivity.navHostFragmentNavigate(R.id.favoritesFragment);
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

    public void switchToMyFavoritesToolBar() {
        this.inflate(R.menu.toolbar_refresh_my_favorites);
    }

    public void switchToMyPostsToolBar() {
        this.inflate(R.menu.toolbar_my_posts);
    }

    private void inflate(int menuRes) {
        this.mainActivity.getMenuInflater().inflate(menuRes, this.menu);
    }
}
