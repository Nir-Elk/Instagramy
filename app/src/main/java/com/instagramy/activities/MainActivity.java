package com.instagramy.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import com.instagramy.R;
import com.instagramy.fragments.GroupsFragment;
import com.instagramy.fragments.MainFragment;
import com.instagramy.fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        GroupsFragment.OnFragmentInteractionListener {
    Fragment homeFragment;
    Fragment settingFragment;
    Fragment groupsFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.homeFragment = new MainFragment();
        this.settingFragment = new SettingsFragment();
        this.groupsFragment = new GroupsFragment();

        switchFragment(homeFragment);

        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(homeFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(settingFragment);
            }
        });

        findViewById(R.id.nav_groups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(groupsFragment);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);



    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
