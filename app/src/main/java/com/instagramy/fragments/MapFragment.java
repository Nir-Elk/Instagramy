package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private PostsList posts;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");

        this.posts = MapFragmentArgs.fromBundle(getArguments()).getPosts();


        if(posts == null || posts.size()==0) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    posts = new PostsList();
                    for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                        try {
                            Post post = postsnap.getValue(Post.class);
                            posts.add(post);
                        } catch (Exception ignored){}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        activity.setSelectedItemBottomNavigation(R.id.nav_map);
        super.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        for (Post post : posts) {
            Double lat = post.getLocationLatitude();
            Double lng = post.getLocationLongitude();
            if(lat != null && lng !=null) {
                LatLng position = new LatLng(lat, lng);
                this.googleMap.addMarker(new MarkerOptions().position(position).title(post.getTitle()));
            }
        }

        Post newestPost = posts.get(posts.size()-1);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(newestPost.getLocationLatitude(),newestPost.getLocationLongitude())));
        this.googleMap.setMinZoomPreference(10);
        this.googleMap.setMaxZoomPreference(10);
    }
}
