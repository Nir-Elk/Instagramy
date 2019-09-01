package com.instagramy.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.instagramy.R;
import com.instagramy.helpers.MainActivityMenuHelper;
import com.instagramy.models.LinkListViewModel;
import com.instagramy.models.Post;
import com.instagramy.models.Profile;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.ProfileRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.utils.GPSLocation;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final String ARGS_SCROLL_Y = "mStateScrollY";
    private Dialog popupAddPost, popupChooseGalleryOrCamera;
    private ImageView popupPostImage, popupAddBtn;
    private TextView popupTitle, popupDescription;
    private ProgressBar popupClickProgress;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1, REQUEST_OPEN_GALLERY = 2, REQUEST_IMAGE_CAPTURE = 3;
    private Uri pickedImgUri = null;
    private BottomNavigationView bottomNavigationView;
    private Uri imageUri;
    private int mStateScrollY;
    private PostRepository postRepository;
    private ProfileRepository profileRepository;
    private AuthRepository authRepository;
    private MainActivityMenuHelper menuHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postRepository = RepositoryManager.getInstance().getPostRepository();
        profileRepository = RepositoryManager.getInstance().getProfileRepository();
        authRepository = RepositoryManager.getInstance().getAuthRepository();

        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        initBottomBarClickListeners();
        initBottomBarClickListeners();
        iniPopup();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        LinkListViewModel.getInstance(this);

        if (savedInstanceState != null) {
            mStateScrollY = savedInstanceState.getInt(ARGS_SCROLL_Y, 0);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGS_SCROLL_Y, mStateScrollY);
    }

    public void setSelectedItemBottomNavigation(final int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);

    }

    public Dialog getPopupChooseGalleryOrCamera() {
        return popupChooseGalleryOrCamera;
    }

    public void initBottomBarClickListeners() {

        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToHomeToolBar();
                navHostFragmentNavigate(R.id.action_global_homeFragment);
            }
        });

        findViewById(R.id.nav_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToHomeToolBar();
                navHostFragmentNavigate(R.id.action_global_mapFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToHomeToolBar();
                navHostFragmentNavigate(R.id.action_global_settingsFragment);
            }
        });

        findViewById(R.id.nav_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToMyFavoritesToolBar();
                navHostFragmentNavigate(R.id.action_global_favoritesFragment);
            }
        });
        findViewById(R.id.nav_my_posts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuHelper.switchToMyPostsToolBar();
                navHostFragmentNavigate(R.id.action_global_myPostsFragment);
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
        popupChooseGalleryOrCamera.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupChooseGalleryOrCamera.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

        Button cameraBtn = popupChooseGalleryOrCamera.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissionForCamera();
            }
        });
        Button galleryBtn = popupChooseGalleryOrCamera.findViewById(R.id.gallery_btn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissionForGallery();
            }
        });


        popupAddPost = new Dialog(this);
        popupAddPost.setContentView(R.layout.popup_add_post);
        popupAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupPostImage = popupAddPost.findViewById(R.id.popup_image);
        popupTitle = popupAddPost.findViewById(R.id.popup_title);
        popupDescription = popupAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popupAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popupAddPost.findViewById(R.id.popup_progressBar);


        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields
                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && popupPostImage != null) {

                    final String path = pickedImgUri.getLastPathSegment();
                    postRepository.uploadPhoto(path, pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            postRepository.getDownloadPhotoUrl(path).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imageDownloadLink = uri.toString();
                                    GPSLocation gpsLocation = new GPSLocation(MainActivity.this);
                                    final Location location = gpsLocation.getLocation();


                                    profileRepository.getProfile(authRepository.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Profile profile = dataSnapshot.getValue(Profile.class);

                                            Post post = new Post(popupTitle.getText().toString(),
                                                    popupDescription.getText().toString(),
                                                    imageDownloadLink,
                                                    authRepository.getCurrentUser().getDisplayName(),
                                                    profile.getName(),
                                                    profile.getImageUri(),
                                                    location);
                                            addPost(post);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

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
        String key = postRepository.createNewPost();
        post.setKey(key);
        postRepository.addPost(post).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestPermissionForGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            openGallery();
        }
    }


    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_OPEN_GALLERY);
    }

    private void checkAndRequestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, "Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            openCamera();
        }
    }


    private void openCamera() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY || requestCode == REQUEST_IMAGE_CAPTURE) {
            popupChooseGalleryOrCamera.dismiss();
            popupAddPost.show();
            popupTitle.setText("");
            popupDescription.setText("");
            switch (requestCode) {
                case REQUEST_OPEN_GALLERY:
                    pickedImgUri = data.getData();
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            pickedImgUri = imageUri;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                default:
                    break;
            }
            popupPostImage.setImageURI(pickedImgUri);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuHelper = new MainActivityMenuHelper(this, menu);
        this.menuHelper.switchToHomeToolBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuHelper.onOptionItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

}
