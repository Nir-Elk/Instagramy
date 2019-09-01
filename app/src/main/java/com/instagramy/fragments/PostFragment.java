package com.instagramy.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;

public class PostFragment extends Fragment {
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private TextView postTitle, postDescription, postUserName, postYummies, postImageErrorMessage;
    private ImageView postImage, postUserImage, postMapBtn, postYummiBtn, postFavoriteBtn;
    private ProgressBar postImageProgressBar;
    private View view;
    private Post post;
    private String postId;
    private MainActivity mainActivity;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
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
        postRepository.getPost(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                if (post == null) {
                    mainActivity.onBackPressed();
                } else {
                    updateView();
                }
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
                    .into(new ImageViewTarget<Drawable>(postImage) {
                        @Override
                        protected void setResource(@Nullable final Drawable resource) {

                            postImage.setImageDrawable(resource);

                            postImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                    View mView = getLayoutInflater().inflate(R.layout.main_photo_dialog, null);
                                    PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                                    photoView.setImageDrawable(resource);
                                    mBuilder.setView(mView);
                                    AlertDialog mDialog = mBuilder.create();
                                    mDialog.show();
                                }
                            });
                        }
                    });
            Glide.with(getContext()).load(post.getUserimg()).into(postUserImage);
        }

        postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = authRepository.getCurrentUser().getEmail();
                postRepository.updateYummies(post.getKey(), post.toggleYummi(email));
                if (post.alreadyYummi(email)) {
                    postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
                } else {
                    postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
                }
            }
        });
        if (post.getUserId().equals(authRepository.getCurrentUser().getDisplayName())) {
            mainActivity.getMenuHelper().switchToEditPostToolBar();
            final NavGraphDirections.ActionGlobalEditPostFragment editPostAction = NavGraphDirections.actionGlobalEditPostFragment(post.getKey());
            mainActivity.getMenuHelper().setEditPostAction(editPostAction);
            mainActivity.getMenuHelper().setDeletePostRunnableAction(new Runnable() {
                @Override
                public void run() {
                    postRepository.deletePost(post.getKey());
                    mainActivity.getMenuHelper().switchToHomeToolBar();
                }
            });
        } else {
            mainActivity.getMenuHelper().switchToHomeToolBar();
        }


        final NavGraphDirections.ActionGlobalMapFragment mapAction = PostFragmentDirections.actionGlobalMapFragment();
        Post.PostList postList = new Post.PostList();
        postList.add(post);
        mapAction.setPosts(postList);
        postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(mapAction));
        final PostFragmentDirections.ActionPostFragmentToProfileFragment profileAction = PostFragmentDirections.actionPostFragmentToProfileFragment(post.getUserId());
        postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
    }

}
