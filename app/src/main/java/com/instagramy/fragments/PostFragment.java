package com.instagramy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.models.Post;
import com.instagramy.services.Firebase;

public class PostFragment extends Fragment {
    private Firebase firebase;
    private TextView postTitle, postDescription, postUserName, postYummies, postImageErrorMessage;
    private ImageView postImage, postUserImage, postMapBtn, postYummiBtn, postFavoriteBtn;
    private ProgressBar postImageProgressBar;
    private View view;
    private Post post;
    private String postId;


    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebase = Firebase.getInstance();
        postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);


        postTitle = view.findViewById(R.id.post_title);
        postDescription = view.findViewById(R.id.post_description);
        postUserName = view.findViewById(R.id.post_username);
        postYummies = view.findViewById(R.id.post_yummies);
        postImage = view.findViewById(R.id.post_img);
        postUserImage = view.findViewById(R.id.post_userimg);
        postMapBtn = view.findViewById(R.id.post_map_btn);
        postYummiBtn = view.findViewById(R.id.post_yummies_btn);
        postFavoriteBtn = view.findViewById(R.id.post_favorite_btn);
        postImageProgressBar = view.findViewById(R.id.post_progressBar);

        firebase.getPost(postId).addValueEventListener(new ValueEventListener() {
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


        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postYummies.setText(String.valueOf(post.getYummies()));
        postUserName.setText(post.getUserName());
        postImageProgressBar.setVisibility(View.INVISIBLE);

//        if (getContext() != null) {
        Glide.with(getContext()).load(post.getUserimg()).into(postUserImage);
        Glide.with(getContext()).load(post.getPicture()).into(postImage);
//        }
        final PostFragmentDirections.ActionPostFragmentToProfileFragment profileAction = PostFragmentDirections.actionPostFragmentToProfileFragment(post.getUserId());
        postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
    }

}
