package com.instagramy.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private Button logoutbtn;
    private Button aboutBtn, editProfileBtn;

    private OnFragmentInteractionListener mListener;

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

        editProfileBtn = fragmentView.findViewById(R.id.edit_profile_btn);
        editProfileBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SettingsFragmentDirections.actionSettingsFragmentToEditProfileFragment()));

        return fragmentView;
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
