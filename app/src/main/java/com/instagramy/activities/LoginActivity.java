package com.instagramy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.instagramy.R;
import com.instagramy.helpers.KeyboardHelper;
import com.instagramy.services.Firebase;
import com.instagramy.utils.Navigator;

public class LoginActivity extends AppCompatActivity {

    private Navigator navigator;
    private EditText userEmail, userPass;
    private ProgressBar loadingProgress;
    private Button LogBtn;
    private TextView LogNewAccount;
    private Firebase firebase;
    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        userEmail = findViewById(R.id.LogEmail);
        userPass = findViewById(R.id.LogPass);
        LogBtn = findViewById(R.id.LogButton);
        loadingProgress = findViewById(R.id.LogprogressBar);
        LogNewAccount = findViewById(R.id.LogNewAccount);
        loadingProgress.setVisibility(View.INVISIBLE);
        firebase = Firebase.getInstance();
        navigator = new Navigator(this);

        LogNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        LogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideGroup();
                loadingProgress.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();

                if (email.isEmpty() || pass.isEmpty()) {
                    showMessage("Please Verify all fields");
                    showGroup();
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    singIn(email, pass);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebase.getCurrentUser() != null) {
            //user is already connected to redirect him to home page
            navigator.navigate(MainActivity.class);

        }
    }

    private void singIn(String email, String pass) {
        firebase.signIn(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingProgress.setVisibility(View.INVISIBLE);
                    navigator.navigate(MainActivity.class);
                } else {
                    showGroup();
                    LogBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    showMessage(task.getException().getMessage());
                }
            }
        });
    }


    void hideGroup() {
        KeyboardHelper.hideKeyboard(this);
        groupToBeHideOrNot(View.INVISIBLE);
    }

    void showGroup() {
        groupToBeHideOrNot(View.VISIBLE);
    }

    void groupToBeHideOrNot(int visibility) {
        LogBtn.setVisibility(visibility);
        userEmail.setVisibility(visibility);
        LogNewAccount.setVisibility(visibility);
        userEmail.setVisibility(visibility);
        userPass.setVisibility(visibility);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("Result");
                if (result.equals("OK"))
                    this.onStart();
            }
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
