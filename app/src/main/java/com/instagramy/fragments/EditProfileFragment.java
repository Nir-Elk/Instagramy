package com.instagramy.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.models.Profile;


public class EditProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private Profile profile;
    private ProgressBar userProgressBar;
    private TextView emailProfile;
    private EditText nameProfile, passProfile, rePassProfile;
    private ImageView userImage;
    private Button updatebtn;

    private OnFragmentInteractionListener mListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        this.database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference().child("Profiles").child((this.mAuth.getCurrentUser().getDisplayName()));

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile = dataSnapshot.getValue(Profile.class);
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
        updatebtn = fragmentView.findViewById(R.id.user_update_profile_btn);
        userProgressBar = fragmentView.findViewById(R.id.user_progressBar_profile);
        userImage = fragmentView.findViewById(R.id.user_img_profile);

        return fragmentView;
    }


    public void updateView(View fragmentView) {

        emailProfile.setText(profile.getEmail());
        nameProfile.setText(profile.getName());
        userProgressBar.setVisibility(View.VISIBLE);
        userImage.setVisibility(View.VISIBLE);

        Glide.with(fragmentView).load(profile.getImageUri()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                userProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                userProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).apply(RequestOptions.circleCropTransform()).into(userImage);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isProfileUpdated = false;
                if (!passProfile.getText().toString().isEmpty()) {

                    if (rePassProfile.getText().toString().isEmpty()) {
                        toast("Please Re enter password");

                    }

                    if (passProfile.getText().toString().equals(rePassProfile.getText().toString()) && passProfile.getText().toString().length() > 5) {
                        mAuth.getCurrentUser().updatePassword(passProfile.getText().toString());
                        isProfileUpdated = true;
                    } else {
                        toast("Please a valid password, longer then 5");
                    }
                }

                if(!nameProfile.getText().toString().equals(profile.getName())) {
                    if (nameProfile.getText().toString().length() > 0) {
                        mDatabaseRef.child("name").setValue(nameProfile.getText().toString());
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
