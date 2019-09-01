package com.instagramy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.R;
import com.instagramy.helpers.NetworkHelper;
import com.instagramy.services.Firebase;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 500; //This is 0.5 seconds
    private AppCompatActivity thisActivity = SplashActivity.this;
    private Intent intent;
    private Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        TextView error = findViewById(R.id.splash_error_no_connection);
        this.firebase = Firebase.getInstance();

        if (NetworkHelper.hasNetworkAccess(getApplicationContext())) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);

            intent = new Intent(thisActivity.getApplicationContext(), LoginActivity.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    thisActivity.startActivity(intent);
                    thisActivity.finish();
                }
            }, SPLASH_TIME);
        } else {
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            error.setText("No Connectivity!");
            error.setVisibility(View.VISIBLE);
        }
    }
}
