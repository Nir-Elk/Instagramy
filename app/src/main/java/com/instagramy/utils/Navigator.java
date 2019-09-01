package com.instagramy.utils;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class Navigator {

    AppCompatActivity activity;

    private Navigator() {
    }

    public Navigator(AppCompatActivity baseActivity) {
        this.activity = baseActivity;
    }

    public void navigate(Class target) {
        this.activity.startActivity(new Intent(this.activity.getApplicationContext(), target));
        this.activity.finish();
    }
}
