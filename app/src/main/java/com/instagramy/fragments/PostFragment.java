package com.instagramy.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;
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

        if (getContext() != null) {
        Glide.with(getContext())
                .load(post.getPicture())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        postImageProgressBar.setVisibility(View.GONE);
                        postImageErrorMessage.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        postImageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(postImage);
            Glide.with(getContext()).load(post.getUserimg()).into(postUserImage);
        }

        postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = firebase.getCurrentUser().getEmail();
                firebase.updateYummies(post.getKey(), post.toggleYummi(email));
                if (post.alreadyYummi(email)) {
                    postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
                } else {
                    postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
                }
            }
        });

        final NavGraphDirections.ActionGlobalMapFragment mapAction = PostFragmentDirections.actionGlobalMapFragment();
        PostsList postsList = new PostsList();
        postsList.add(post);
        mapAction.setPosts(postsList);
        postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(mapAction));

        final PostFragmentDirections.ActionPostFragmentToProfileFragment profileAction = PostFragmentDirections.actionPostFragmentToProfileFragment(post.getUserId());
        postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
    }

}
