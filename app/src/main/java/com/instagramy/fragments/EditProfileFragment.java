package com.instagramy.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.DrawableResource;
import com.instagramy.models.Profile;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.ProfileRepository;
import com.instagramy.repositories.RepositoryManager;


public class EditProfileFragment extends ActionBarFragment {
    private Profile profile;
    private ProgressBar userProgressBar;
    private TextView emailProfile;
    private EditText nameProfile, passProfile, rePassProfile;
    private ImageView userImage;
    private Button updateBtn, deleteBtn;
    private ProfileRepository profileRepository;
    private AuthRepository authRepository;
    private MainActivity mainActivity;
    private DrawableRepository drawableRepository;
    private int deleteCounter = 10;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawableRepository = RepositoryManager.getInstance().getDrawableRepository((AppCompatActivity) getActivity());
        profileRepository = RepositoryManager.getInstance().getProfileRepository();
        authRepository = RepositoryManager.getInstance().getAuthRepository();
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.setTitle("Edit Profile");
        mainActivity.getBottomNavigationHelper().setSelectedItemBottomNavigation(R.id.nav_settings);
        mainActivity.getMenuHelper().switchToSettingsToolBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileRepository.getProfile(authRepository.getCurrentUser().getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile = dataSnapshot.getValue(Profile.class);
                if (profile != null)
                    updateView(fragmentView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        emailProfile = fragmentView.findViewById(R.id.user_email_profile);
        nameProfile = fragmentView.findViewById(R.id.user_name_profile);
        passProfile = fragmentView.findViewById(R.id.user_pass_profile);
        rePassProfile = fragmentView.findViewById(R.id.user_repass_profile);
        updateBtn = fragmentView.findViewById(R.id.user_update_profile_btn);
        userProgressBar = fragmentView.findViewById(R.id.user_progressBar_profile);
        userImage = fragmentView.findViewById(R.id.user_img_profile);
        deleteBtn = fragmentView.findViewById(R.id.user_delete_profile_btn);
        return fragmentView;
    }

    private void updateView(final View fragmentView) {
        emailProfile.setText(profile.getEmail());
        nameProfile.setText(profile.getName());
        userProgressBar.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);

        final String profilePictureUrl = profile.getImageUri();
        drawableRepository.getDrawableResource(profilePictureUrl.hashCode()).observe(this, new Observer<DrawableResource>() {
            @Override
            public void onChanged(DrawableResource drawableResource) {
                if (drawableResource == null) {
                    Glide.with(fragmentView).load(profilePictureUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            userProgressBar.setVisibility(View.GONE);
                            drawableRepository.insertDrawable(new DrawableResource(profilePictureUrl.hashCode(), resource));
                            return false;
                        }
                    }).apply(RequestOptions.circleCropTransform()).into(userImage);

                } else {
                    Glide.with(fragmentView).load(drawableResource.getDrawable()).apply(RequestOptions.circleCropTransform()).into(userImage);
                    userProgressBar.setVisibility(View.GONE);

                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteCounter == 0) {
                    ((MainActivity) getActivity()).getDialogsHelper().getDeleteProfile().show();
                } else {
                    mainActivity.showMessage("Are you sure? keep clicking (" + --deleteCounter + ")");
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isProfileUpdated = false;
                if (!passProfile.getText().toString().isEmpty()) {

                    if (rePassProfile.getText().toString().isEmpty()) {
                        toast("Please Re enter password");
                    }

                    if (passProfile.getText().toString().equals(rePassProfile.getText().toString()) && passProfile.getText().toString().length() > 5) {
                        authRepository.changePass(passProfile.getText().toString());
                        isProfileUpdated = true;
                    } else {
                        toast("Please a valid password, longer then 5");
                    }
                }

                if (!nameProfile.getText().toString().equals(profile.getName())) {
                    if (nameProfile.getText().toString().length() > 0) {
                        profileRepository.changeName(authRepository.getCurrentUser().getUid(), nameProfile.getText().toString());
                        profile.setName(nameProfile.getText().toString());
                        isProfileUpdated = true;
                    } else {
                        toast("Please select a valid name");
                    }
                }

                passProfile.setText("");
                rePassProfile.setText("");

                if (isProfileUpdated) {
                    toast("Profile has been updated");
                } else {
                    toast("Nothing to update.");
                }
            }
        });
    }


    private void toast(String msg) {
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG)
                .show();
    }

}
