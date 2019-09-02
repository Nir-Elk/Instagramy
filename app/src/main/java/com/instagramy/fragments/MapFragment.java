package com.instagramy.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.view.models.PostListViewModel;

import java.util.HashMap;
import java.util.Map;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    PostListViewModel postListViewModel;
    Post.PostList posts;
    private GoogleMap googleMap;
    private Map<String, String> postMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Map");
        this.posts = MapFragmentArgs.fromBundle(getArguments()).getPosts();

        if (posts == null || posts.size() == 0) {
            postListViewModel.getLiveData().observe(this, new Observer<Post.PostList>() {
                @Override
                public void onChanged(Post.PostList anotherPosts) {
                    posts = anotherPosts;
                    if (googleMap != null) {
                        redrawMap();
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        activity.getMenuHelper().switchToHomeToolBar();
        activity.getBottomNavigationHelper().setSelectedItemBottomNavigation(R.id.nav_map);
        super.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.redrawMap();
    }


    @SuppressLint("CheckResult")
    void redrawMap() {
        googleMap.clear();
        postMap = new HashMap<>();

        // Tel Aviv
        LatLng centerMap = new LatLng(32.0853, 34.7818);
        if (posts != null) {
            for (final Post post : posts) {
                Double lat = post.getLocationLatitude();
                Double lng = post.getLocationLongitude();
                if (lat != null && lng != null) {
                    LatLng position = new LatLng(lat, lng);

                    Marker marker = googleMap.addMarker(
                            new MarkerOptions()
                                    .position(position)
                                    .title(post.getTitle())
                                    .snippet(post.getDescription())
                    );
                    postMap.put(marker.getId(), post.getKey());
                }
            }

            if (posts.size() > 0) {
                Post newestPost = posts.get(posts.size() - 1);
                centerMap = new LatLng(newestPost.getLocationLatitude(), newestPost.getLocationLongitude());
            }
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(
                        NavGraphDirections.actionGlobalPostFragment(
                                postMap.get(marker.getId())
                        )
                );
            }
        });
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerMap));
        this.googleMap.setMinZoomPreference(10);
        this.googleMap.setMaxZoomPreference(10);
    }


}
