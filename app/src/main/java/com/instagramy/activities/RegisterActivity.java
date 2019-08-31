package com.instagramy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.UploadTask;
import com.instagramy.R;
import com.instagramy.models.Profile;
import com.instagramy.services.Firebase;
import com.instagramy.utils.Navigator;

public class RegisterActivity extends AppCompatActivity {

    private Navigator navigator;
    private EditText userEmail, userName, userPassword, userRePassword;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private ImageView userPhoto;
    private static int PReqCode = 1;
    private static int REQUSECODE = 1;
    private Uri pickedImgUri;
    private Intent intent;

    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userPhoto = findViewById(R.id.RegImage);
        userName = findViewById(R.id.RegName);
        userEmail = findViewById(R.id.RegEmail);
        userPassword = findViewById(R.id.RegPass);
        userRePassword = findViewById(R.id.RegrePass);
        regBtn = findViewById(R.id.RegButton);
        loadingProgress = findViewById(R.id.RegProgressBar);
        intent = new Intent();

        loadingProgress.setVisibility(View.INVISIBLE);

        firebase = Firebase.getInstance();
        navigator = new Navigator(this);

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString();
                final String name = userName.getText().toString();
                final String pass = userPassword.getText().toString();
                final String repass = userRePassword.getText().toString();

                if (email.isEmpty() || name.isEmpty() || pass.isEmpty() || !pass.equals(repass)) {
                    showMessage("Please Verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    CreateUserAccount(email, name, pass);
                }

            }
        });
    }

    private void CreateUserAccount(String email, final String name, String pass) {
        firebase.CreateUserAuth(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showMessage("Account created");

                    updateUserInfo(name, pickedImgUri);
                } else {
                    showMessage(task.getException() != null ? task.getException().getMessage() : "Error");
                    loadingProgress.setVisibility(View.INVISIBLE);
                    regBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUSECODE);
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept for requierd permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUSECODE && data != null) {
            pickedImgUri = data.getData();
            userPhoto.setImageURI(pickedImgUri);
        }
    }


    private void updateUserInfo(final String name, Uri pickedImgUri) {
        final String path = pickedImgUri.getLastPathSegment();
        firebase.uploadUserPhoto(path, pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                firebase.getDownloadUserPhotoUrl(path).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Profile profile = new Profile(name, firebase.getCurrentUser().getUid(), firebase.getCurrentUser().getEmail(), uri.toString());
                        addProfile(profile);
                    }
                });
            }
        });
    }


    private void addProfile(Profile profile) {
        final String key = firebase.createNewProfile();
        profile.setKey(key);

        firebase.addProfile(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebase.updateUserAuthKey(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showMessage("Register complete");
                        intent.putExtra("Result", "OK");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
            }
        });

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
