package com.instagramy.helpers.main.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.models.Profile;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.ProfileRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.utils.GPSLocation;

public class MainActivityDialogsHelper {
    private Dialog popupAddPost;
    private Dialog popupChooseGalleryOrCamera;
    private ImageView popupPostImage, popupAddBtn;
    private TextView popupTitle, popupDescription;
    private ProgressBar popupClickProgress;
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private ProfileRepository profileRepository;

    private MainActivity mainActivity;
    private Uri pickedImageUri;

    private MainActivityDialogsHelper() {
    }

    public MainActivityDialogsHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
        this.profileRepository = RepositoryManager.getInstance().getProfileRepository();
        this.initPopup();
    }

    public void dismissAndSwitchToCreatePostPopup(Uri pickedImageUri) {
        this.pickedImageUri = pickedImageUri;
        popupPostImage.setImageURI(pickedImageUri);
        popupChooseGalleryOrCamera.dismiss();
        popupAddPost.show();
        popupTitle.setText("");
        popupDescription.setText("");
    }

    public void initPopup() {

        popupChooseGalleryOrCamera = new Dialog(mainActivity);
        popupChooseGalleryOrCamera.setContentView(R.layout.popup_choose_gallery_or_camera);
        popupChooseGalleryOrCamera.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupChooseGalleryOrCamera.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupChooseGalleryOrCamera.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

        Button cameraBtn = popupChooseGalleryOrCamera.findViewById(R.id.camera_btn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getController().checkAndRequestPermissionForCamera();
            }
        });
        Button galleryBtn = popupChooseGalleryOrCamera.findViewById(R.id.gallery_btn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getController().checkAndRequestPermissionForGallery();
            }
        });


        popupAddPost = new Dialog(this.mainActivity);
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

                    final String path = pickedImageUri.getLastPathSegment();
                    postRepository.uploadPhoto(path, pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            postRepository.getDownloadPhotoUrl(path).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imageDownloadLink = uri.toString();
                                    GPSLocation gpsLocation = new GPSLocation(mainActivity);
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
                                    mainActivity.showMessage(e.getMessage());
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    mainActivity.showMessage("Please verify all input and choose post image");
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
                mainActivity.showMessage("Post Added");
                popupAddBtn.setVisibility(View.VISIBLE);
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddPost.dismiss();
            }
        });
    }

    public Dialog getPopupChooseGalleryOrCamera() {
        return popupChooseGalleryOrCamera;
    }
}
