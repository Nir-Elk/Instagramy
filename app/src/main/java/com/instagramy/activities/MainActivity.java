package com.instagramy.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.R;
import com.instagramy.fragments.FavoritesFragment;
import com.instagramy.fragments.MainFragment;
import com.instagramy.fragments.PostFragment;
import com.instagramy.fragments.ProfileFragment;
import com.instagramy.fragments.SearchFragment;
import com.instagramy.fragments.SettingsFragment;
import com.instagramy.models.Post;
import com.instagramy.models.Profile;
import com.instagramy.utils.GPSLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final String ARGS_SCROLL_Y = "mStateScrollY";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Dialog popupAddPost, popupChooseGalleryOrCamera;
    private ImageView popupPostImage, popupAddBtn;
    private TextView popupTitle, popupDescription;
    private ProgressBar popupClickProgress;
    private Button cameraBtn, galleryBtn;
    private static final int PReqCode = 2, REQUSECODEG = 2, REQUSECODEC = 3;
    private Uri pickedImgUri = null;
    private BottomNavigationView bottomNavigationView;
    private Set<Integer> bottomNavigationItems;
    private Uri imageUri;
    private int mStateScrollY;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        initBottomBarClickListeners();
        iniPopup();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationItems = new HashSet<>();
        bottomNavigationItems.add(R.id.nav_home);
        bottomNavigationItems.add(R.id.nav_favorites);
        bottomNavigationItems.add(R.id.nav_search);
        bottomNavigationItems.add(R.id.nav_settings);
        bottomNavigationItems.add(R.id.nav_map);

        if(savedInstanceState != null){
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

        for (Integer bottomNavigationItem : bottomNavigationItems) {
            if (bottomNavigationItem.equals(itemId)) {
                findViewById(bottomNavigationItem).setClickable(false);

            } else {
                findViewById(bottomNavigationItem).setClickable(true);
            }
        }

    }

    public void initBottomBarClickListeners() {
        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_homeFragment);
            }
        });


        findViewById(R.id.nav_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_mapFragment);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_settingsFragment);
            }
        });

        findViewById(R.id.nav_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navHostFragmentNavigate(R.id.action_global_favoritesFragment);
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

    public void navHostFragmentNavigate(NavDirections directions) {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(directions);
    }

    private void iniPopup() {
        popupChooseGalleryOrCamera = new Dialog(this);
        popupChooseGalleryOrCamera.setContentView(R.layout.popup_choose_gallery_or_camera);
        popupChooseGalleryOrCamera.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupChooseGalleryOrCamera.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupChooseGalleryOrCamera.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

        cameraBtn = popupChooseGalleryOrCamera.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissionForCamera();
            }
        });
        galleryBtn = popupChooseGalleryOrCamera.findViewById(R.id.gallery_btn);
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

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imageDownloadLink = uri.toString();
                                    GPSLocation gpsLocation = new GPSLocation(MainActivity.this);
                                    final Location location = gpsLocation.getLocation();


                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getDisplayName());
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Profile profile = dataSnapshot.getValue(Profile.class);

                                            Post post = new Post(popupTitle.getText().toString(),
                                                    popupDescription.getText().toString(),
                                                    imageDownloadLink,
                                                    currentUser.getDisplayName(),
                                                    profile.getName(),
                                                    profile.getImageUri(),
                                                    location);
                                            addPost(post);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

//                                        Post post = new Post(popupTitle.getText().toString(),
//                                            popupDescription.getText().toString(),
//                                            imageDownloadLink,
//                                            currentUser.getDisplayName(),
//                                            location);
//                                    addPost(post);
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
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestPermissionForGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }


    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUSECODEG);
    }

    private void checkAndRequestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, "Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        PReqCode);
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
            startActivityForResult(intent, REQUSECODEC);

        }


        //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUSECODEC);

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

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSECODEG || requestCode == REQUSECODEC) {
            popupChooseGalleryOrCamera.dismiss();
            popupAddPost.show();
            popupTitle.setText("");
            popupDescription.setText("");
            switch (requestCode) {
                case REQUSECODEG:
                    pickedImgUri = data.getData();
                    break;
                case REQUSECODEC:
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
