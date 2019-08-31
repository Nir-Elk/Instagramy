package com.instagramy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.models.Post;

public class PostFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private TextView title, description, username, yummies;
    private ImageView postImg, userImg;
    private View view;
    private Post post;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
        this.database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference().child("Posts").child(postId);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);


        title = view.findViewById(R.id.post_title);
        description = view.findViewById(R.id.post_description);
        username = view.findViewById(R.id.post_username);
        yummies = view.findViewById(R.id.post_yummies);
        postImg = view.findViewById(R.id.post_img);
        userImg = view.findViewById(R.id.post_userimg);
        Button yummiBtn = view.findViewById(R.id.post_yummies_btn);
        Button mapBtn = view.findViewById(R.id.post_map_btn);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                updateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    public void updateView() {


        title.setText(post.getTitle());
        description.setText(post.getDescription());
        yummies.setText(String.valueOf(post.getYummies()));

        username.setText(post.getUserName());
        if (getContext() != null) {
            Glide.with(getContext()).load(post.getUserimg()).into(userImg);
            Glide.with(getContext()).load(post.getPicture()).into(postImg);
        }
        final PostFragmentDirections.ActionPostFragmentToProfileFragment profileAction = PostFragmentDirections.actionPostFragmentToProfileFragment(post.getUserId());
        username.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        userImg.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
    }

}
