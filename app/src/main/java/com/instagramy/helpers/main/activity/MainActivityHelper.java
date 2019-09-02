package com.instagramy.helpers.main.activity;

import android.view.View;

import com.instagramy.activities.MainActivity;

public class MainActivityHelper {
    protected MainActivity mainActivity;

    public MainActivityHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected void navigate(int action) {
        this.mainActivity.getController().navHostFragmentNavigate(action);
    }

    protected View findViewById(int id) {
        return mainActivity.findViewById(id);
    }
}
