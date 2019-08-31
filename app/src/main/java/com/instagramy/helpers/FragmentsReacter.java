package com.instagramy.helpers;

import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.ErrorMessageFragment;
import com.instagramy.fragments.EditProfileFragment;
import com.instagramy.fragments.PostFragment;
import com.instagramy.fragments.ProfileFragment;
import com.instagramy.fragments.SettingsFragment;

public abstract class FragmentsReacter extends AppCompatActivity implements
        SettingsFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener,
        ErrorMessageFragment.OnFragmentInteractionListener {
}
