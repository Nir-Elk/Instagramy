package com.instagramy.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.models.Profile;
import com.instagramy.repositories.ProfileRepository;
import com.instagramy.repositories.RepositoryManager;

public class ProfileFragment extends Fragment {

    private String profileId;
    private Profile profile;
    private TextView fullName, email;
    private ImageView imageProfile;
    private ProgressBar progressBarProfile;
    private ProfileRepository profileRepository;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.profileRepository = RepositoryManager.getInstance().getProfileRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getArguments() != null;
        this.profileId = ProfileFragmentArgs.fromBundle(getArguments()).getProfileId();

        this.progressBarProfile = view.findViewById(R.id.profile_progressBar);
        this.imageProfile = view.findViewById(R.id.profile_image);
        this.fullName = view.findViewById(R.id.profile_full_name);
        this.email = view.findViewById(R.id.profile_email);
        profileRepository.getProfile(this.profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile = dataSnapshot.getValue(Profile.class);
                updateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    private void updateView() {
        fullName.setText(profile.getName());
        email.setText(profile.getEmail());
        progressBarProfile.setVisibility(View.VISIBLE);

        Glide.with(getContext()).load(profile.getImageUri()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBarProfile.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBarProfile.setVisibility(View.GONE);
                return false;
            }
        }).apply(RequestOptions.circleCropTransform()).into(new ImageViewTarget<Drawable>(imageProfile) {
            @Override
            protected void setResource(@Nullable final Drawable resource) {

                imageProfile.setImageDrawable(resource);

                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getLayoutInflater().inflate(R.layout.main_photo_dialog, null);
                        PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                        photoView.setImageDrawable(resource);
                        mBuilder.setView(mView);
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
            }
        });
    }
}
