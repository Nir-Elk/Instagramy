package com.instagramy.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.instagramy.fragments.MainFragment;
import com.instagramy.fragments.SettingsFragment;
import com.instagramy.models.Post;
import com.instagramy.R;


import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Dialog popupAddPost;
    private ImageView popupUserImage,popupPostImage,popupAddBtn;
    private TextView popupTitle,popupDescription;
    private ProgressBar popupClickProgress;
    private static final int PReqCode = 2,REQUSECODE = 2;
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        iniPopup();
        setupPopupImageClick();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddPost.show();
            }
        });
    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission();
            }
        });
    }

    private void iniPopup() {
        popupAddPost = new Dialog(this);
        popupAddPost.setContentView(R.layout.popup_add_post);
        popupAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popupAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popupAddPost.findViewById(R.id.popup_image);
        popupTitle = popupAddPost.findViewById(R.id.popup_title);
        popupDescription = popupAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popupAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popupAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(MainActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        // add post

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields
                if(!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && popupPostImage != null){

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    Post post = new Post(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid());
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });


                } else {
                    showMessage("Please verify all input and choose post image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }




        });



    }
    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();
        post.setKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added");
                popupAddBtn.setVisibility(View.VISIBLE);
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
    }
    private void checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(MainActivity.this,"Please accept for requierdpermission",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }else{
            openGallery();
        }
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUSECODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUSECODE && data != null){
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();
        } else if (id == R.id.nav_admin_settings) {
            // TODO: hide if not admin
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent logginActivity = new Intent(this,LoginActivity.class);
            startActivity(logginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.nav_userEmail);
        TextView name = headerView.findViewById(R.id.nav_userName);
        ImageView image = headerView.findViewById(R.id.nav_userImage);
        email.setText(currentUser.getEmail());
        name.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(image);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
