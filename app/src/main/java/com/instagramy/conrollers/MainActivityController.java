package com.instagramy.conrollers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;

import static com.instagramy.constants.MainActivityConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.instagramy.constants.MainActivityConstants.REQUEST_IMAGE_CAPTURE;
import static com.instagramy.constants.MainActivityConstants.REQUEST_OPEN_GALLERY;
import static com.instagramy.constants.MainActivityConstants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;

public class MainActivityController {
    private MainActivity mainActivity;

    private Uri imageUri;

    private MainActivityController() {

    }

    public MainActivityController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void checkAndRequestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)) {
                Toast.makeText(mainActivity, "Please accept for requierdpermission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            openCamera();
        }
    }


    private void openCamera() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = mainActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            mainActivity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        }
    }

    public Uri onPickImageResult(int requestCode, int resultCode, Intent data) {
        Uri pickedImgUri = null;


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

        return pickedImgUri;
    }

    public void checkAndRequestPermissionForGallery() {
        if (ContextCompat.checkSelfPermission(this.mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                mainActivity.showMessage("Please accept for requierdpermission");
            } else {
                ActivityCompat.requestPermissions(this.mainActivity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        mainActivity.startActivityForResult(galleryIntent, REQUEST_OPEN_GALLERY);
    }

    public void navHostFragmentNavigate(int fragmentId) {
        Navigation.findNavController(mainActivity.findViewById(R.id.nav_host_fragment)).navigate(fragmentId);
    }
}
