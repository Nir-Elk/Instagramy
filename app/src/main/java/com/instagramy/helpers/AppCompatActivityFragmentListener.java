package com.instagramy.helpers;

import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.fragments.EditProfileFragment;
import com.instagramy.fragments.FavoritesFragment;
import com.instagramy.fragments.MainFragment;
import com.instagramy.fragments.MyPostsFragment;
import com.instagramy.fragments.PostFragment;
import com.instagramy.fragments.ProfileFragment;
import com.instagramy.fragments.SettingsFragment;

public abstract class AppCompatActivityFragmentListener extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        PostFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        MyPostsFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener {
}
