package com.instagramy.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instagramy.R;
import com.instagramy.utils.Navigator;

public class LoginActivity extends AppCompatActivity {

    private Navigator navigator;
    private EditText userEmail,userPass;
    private ProgressBar loadingProgress;
    private Button LogBtn;
    private TextView LogNewAccount;
    private FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();
        navigator = new Navigator(this);

        LogNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        LogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();

                if(email.isEmpty() || pass.isEmpty()){
                    showMessage("Please Verify all fields");
                    LogBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else{
                    singIn(email,pass);
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //user is already connected to redirect him to home page
            navigator.navigate(MainActivity.class);

        }
    }

    private void singIn(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    LogBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    navigator.navigate(MainActivity.class);
                }
                else{
                    LogBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    showMessage(task.getException().getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("Result");
                if(result.equals("OK"))
                    this.onStart();
            }
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }
}
