package com.instagramy.utils;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Navigator {

    private Navigator() {}

    AppCompatActivity activity;

    public Navigator(AppCompatActivity baseActivity) {
        this.activity = baseActivity;
    }

    public void navigate(Class target) {
        //Intent intent = ;
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.activity.startActivity(new Intent(this.activity.getApplicationContext(), target));
        this.activity.finish();
    }
}
