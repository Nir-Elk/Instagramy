package com.instagramy.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.R;
import com.instagramy.fragments.SearchFragment;
import com.instagramy.fragments.GroupsFragment;
import com.instagramy.fragments.MainFragment;
import com.instagramy.fragments.MapFragment;
import com.instagramy.fragments.PostFragment;
import com.instagramy.fragments.ProfileFragment;
import com.instagramy.fragments.SettingsFragment;
import com.instagramy.models.Post;
import com.instagramy.utils.GPSLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        GroupsFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Dialog popupAddPost,popupChooseGalleryOrCamera;
    private ImageView popupUserImage,popupPostImage,popupAddBtn;
    private TextView popupTitle,popupDescription;
    private ProgressBar popupClickProgress;
    private Button cameraBtn, galleryBtn;
    private static final int PReqCode = 2,REQUSECODEG = 2,REQUSECODEC = 3;
    private Uri pickedImgUri = null;
    private BottomNavigationView bottomNavigationView;
    private Set<Integer> bottomNavigationItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        initBottomBarClickListeners();
        iniPopup();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationItems = new HashSet<>();
        bottomNavigationItems.add(R.id.nav_home);
        bottomNavigationItems.add(R.id.nav_groups);
        bottomNavigationItems.add(R.id.nav_search);
        bottomNavigationItems.add(R.id.nav_settings);
    }

    public void setSelectedItemBottomNavigation(final int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
        bottomNavigationItems.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                if(integer.equals(itemId)) {
                    findViewById(integer).setClickable(false);
                } else {
                    findViewById(integer).setClickable(true);
                }
            }
        });


    }
    public void initBottomBarClickListeners() {
        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_homeFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_settingsFragment);
            }
        });

        findViewById(R.id.nav_groups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navHostFragmentNavigate(R.id.action_global_groupsFragment);
            }
        });
        findViewById(R.id.nav_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_searchFragment);
            }
        });
    }

    public void navHostFragmentNavigate(int fragmentId) {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(fragmentId);
    }

    private void iniPopup() {
        popupChooseGalleryOrCamera = new Dialog(this);
        popupChooseGalleryOrCamera.setContentView(R.layout.popup_choose_gallery_or_camera);
        popupChooseGalleryOrCamera.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupChooseGalleryOrCamera.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popupChooseGalleryOrCamera.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

        cameraBtn = popupChooseGalleryOrCamera.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissionForCamera();
            }
        });        galleryBtn = popupChooseGalleryOrCamera.findViewById(R.id.gallery_btn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissionForGallery();
            }
        });



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
                                    GPSLocation gpsLocation = new GPSLocation(MainActivity.this);
                                    Location location = gpsLocation.getLocation();
                                    Post post = new Post(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getDisplayName(),
                                            currentUser.getPhotoUrl().toString(),
                                            location);
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

    private void checkAndRequestPermissionForGallery() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(MainActivity.this,"Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
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
        startActivityForResult(galleryIntent,REQUSECODEG);
    }

    private void checkAndRequestPermissionForCamera() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)){
                Toast.makeText(MainActivity.this,"Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PReqCode);
            }
        }else{
            openCamera();
        }
    }

    private void openCamera() {
        //TODO: open Camera intent and wait for user to pick an image
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUSECODEC);

    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUSECODEG || requestCode == REQUSECODEC){
            popupChooseGalleryOrCamera.dismiss();
            popupAddPost.show();
            popupTitle.setText("");
            popupDescription.setText("");
            switch (requestCode) {
                case REQUSECODEG:
                    pickedImgUri = data.getData();
                    break;
                case REQUSECODEC:
                    pickedImgUri = bitmapToUriConverter((Bitmap)data.getExtras().get("data"));

                    break;
                default:
                    break;
            }
            popupPostImage.setImageURI(pickedImgUri);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_post_btn:
                popupChooseGalleryOrCamera.show();
                break;

                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

}
