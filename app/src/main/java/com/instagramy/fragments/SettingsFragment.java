package com.instagramy.fragments;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.LoginActivity;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Profile;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;

    private ProgressBar userProgressBar;
    private TextView emailProfile;
    private EditText nameProfile, passProfile, rePassProfile;
    private ImageView userImage;
    private Button updatebtn;
    private Button logoutbtn;
    private Button aboutBtn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private Profile profile;
    private View view;


    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setSelectedItemBottomNavigation(R.id.nav_settings);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        view = fragmentView;
        this.database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference().child("Profiles").child(mAuth.getCurrentUser().getDisplayName());

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

    public void updateView(View fragmentView){

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
        }).into(userImage);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passProfile.getText().toString().equals(rePassProfile.getText().toString()) && passProfile.getText().toString().length()>5)
                {
                    mAuth.getCurrentUser().updatePassword(passProfile.getText().toString());

                    // TODO: add notification
                }
                if(nameProfile.getText().toString().length()>0 && !nameProfile.getText().toString().equals(profile.getName()))
                {
                    mDatabaseRef.child("name").setValue(nameProfile.getText().toString());
                    profile.setName(nameProfile.getText().toString());

                    // TODO: add notification
                }
                passProfile.setText("");
                rePassProfile.setText("");
            }
        });

        logoutbtn = fragmentView.findViewById(R.id.logout_btn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logginActivity = new Intent(getActivity(), LoginActivity.class);
                startActivity(logginActivity);
                getActivity().finish();
            }
        });

        aboutBtn = fragmentView.findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SettingsFragmentDirections.actionSettingsFragmentToAboutActivity()));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
