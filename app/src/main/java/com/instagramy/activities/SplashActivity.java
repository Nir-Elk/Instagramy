package com.instagramy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.instagramy.R;
import com.instagramy.utils.Navigator;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 2500; //This is 4 seconds
    private AppCompatActivity thisActivity = SplashActivity.this;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        intent = new Intent(thisActivity.getApplicationContext(), LoginActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thisActivity.startActivity(intent);
                thisActivity.finish();
            }
        }, SPLASH_TIME);
    }
}
