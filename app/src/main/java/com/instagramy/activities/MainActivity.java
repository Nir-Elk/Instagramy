package com.instagramy.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.instagramy.R;
import com.instagramy.conrollers.MainActivityController;
import com.instagramy.helpers.main.activity.MainActivityBottomNavigationHelper;
import com.instagramy.helpers.main.activity.MainActivityDialogsHelper;
import com.instagramy.helpers.main.activity.MainActivityMenuHelper;
import com.instagramy.models.LinkListViewModel;

import static com.instagramy.constants.MainActivityConstants.ARGS_SCROLL_Y;
import static com.instagramy.constants.MainActivityConstants.REQUEST_IMAGE_CAPTURE;
import static com.instagramy.constants.MainActivityConstants.REQUEST_OPEN_GALLERY;


public class MainActivity extends AppCompatActivity {
    private int mStateScrollY;
    private MainActivityMenuHelper menuHelper;
    private MainActivityDialogsHelper dialogsHelper;
    private MainActivityController controller;
    private MainActivityBottomNavigationHelper bottomNavigationHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        this.controller = new MainActivityController(this);
        this.dialogsHelper = new MainActivityDialogsHelper(this);
        this.bottomNavigationHelper = new MainActivityBottomNavigationHelper(this);
        LinkListViewModel.getInstance(this);

        if (savedInstanceState != null) {
            mStateScrollY = savedInstanceState.getInt(ARGS_SCROLL_Y, 0);
        }

    }

    public MainActivityBottomNavigationHelper getBottomNavigationHelper() {
        return bottomNavigationHelper;
    }

    public MainActivityMenuHelper getMenuHelper() {
        return menuHelper;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_SCROLL_Y, mStateScrollY);
    }

    public MainActivityController getController() {
        return controller;
    }

    public MainActivityDialogsHelper getDialogsHelper() {
        return dialogsHelper;
    }

    public void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_GALLERY || requestCode == REQUEST_IMAGE_CAPTURE) {
            Uri pickedImageUri = controller.onPickImageResult(requestCode, resultCode, data);
            if (pickedImageUri != null) {
                dialogsHelper.dismissAndSwitchToCreatePostPopup(pickedImageUri);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuHelper = new MainActivityMenuHelper(this, menu);
        this.menuHelper.switchToHomeToolBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuHelper.onOptionItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

}
