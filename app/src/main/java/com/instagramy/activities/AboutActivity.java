package com.instagramy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.R;

public class AboutActivity extends AppCompatActivity {
    int countDown = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        final TextView iluzGay = findViewById(R.id.iluz_gay);

        getSupportActionBar().hide();
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.super.onBackPressed();
            }
        });

        findViewById(R.id.textView10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (--countDown == 0) {
                    if (iluzGay.getVisibility() == View.VISIBLE) {
                        iluzGay.setVisibility(View.INVISIBLE);
                    } else {
                        iluzGay.setVisibility(View.VISIBLE);
                    }
                    countDown = 10;
                } else if (countDown < 7) {
                    Toast.makeText(getApplicationContext(), "Only more " + countDown + " Steps!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
