package com.instagramy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.instagramy.R;
import com.instagramy.activities.LoginActivity;
import com.instagramy.activities.MainActivity;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
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

        Button logoutbtn = fragmentView.findViewById(R.id.logout_btn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logginActivity = new Intent(getActivity(), LoginActivity.class);
                startActivity(logginActivity);
                getActivity().finish();
            }
        });

        Button aboutBtn = fragmentView.findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SettingsFragmentDirections.actionSettingsFragmentToAboutActivity()));

        Button editProfileBtn = fragmentView.findViewById(R.id.edit_profile_btn);
        editProfileBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SettingsFragmentDirections.actionSettingsFragmentToEditProfileFragment()));

        return fragmentView;
    }

}
