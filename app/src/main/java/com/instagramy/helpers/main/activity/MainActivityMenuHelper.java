package com.instagramy.helpers.main.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.navigation.Navigation;

import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.activities.LoginActivity;
import com.instagramy.activities.MainActivity;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.RepositoryManager;

public class MainActivityMenuHelper extends MainActivityHelper {
    private AuthRepository authRepository;
    private Menu menu;
    private NavGraphDirections.ActionGlobalEditPostFragment editPostAction;
    private Runnable deletePostRunnableAction;
    private boolean areYouSure = false;

    public MainActivityMenuHelper(MainActivity mainActivity, Menu menu) {
        super(mainActivity);
        this.menu = menu;
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
    }

    public void setEditPostAction(NavGraphDirections.ActionGlobalEditPostFragment editPostAction) {
        this.editPostAction = editPostAction;
    }

    public void setDeletePostRunnableAction(Runnable deletePostRunnableAction) {
        this.deletePostRunnableAction = deletePostRunnableAction;
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

            case R.id.menu_settings:
                navigate(R.id.settingsFragment);
                break;

            case R.id.menu_delete_post:
                if (areYouSure) {
                    areYouSure = false;
                    deletePostRunnableAction.run();
                } else {
                    mainActivity.showMessage("Are you sure? click again.");
                    areYouSure = true;
                }
                break;

            case R.id.menu_edit_post:
                Navigation.findNavController(mainActivity, R.id.nav_host_fragment).navigate(editPostAction);
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

    public void switchToEditPostToolBar() {
        this.inflate(R.menu.toolbar_edit_post);
    }

    public void switchToSettingsToolBar() {
        this.inflate(R.menu.toolbar_settings);
    }

    public void switchToMyFavoritesToolBar() {
        this.inflate(R.menu.toolbar_refresh_my_favorites);
    }

    public void switchToMyPostsToolBar() {
        this.inflate(R.menu.toolbar_my_posts);
    }

    private void inflate(int menuRes) {
        this.menu.clear();
        this.mainActivity.getMenuInflater().inflate(menuRes, this.menu);
    }

    public void onBackPressed(int selectedItemBottomNavigation) {

        switch (selectedItemBottomNavigation) {
            case R.id.nav_home:
            case R.id.nav_map:
                switchToHomeToolBar();
                break;
            case R.id.nav_favorites:
                switchToMyFavoritesToolBar();
                break;
            case R.id.nav_my_posts:
                switchToMyPostsToolBar();
                break;
            case R.id.nav_settings:
                switchToSettingsToolBar();
                break;

            default:
                break;
        }
    }
}
