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
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.DrawableResource;
import com.instagramy.models.Favorite;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.utils.HashSets;
import com.instagramy.view.models.FavoritesViewModel;

import java.util.List;

public class PostFragment extends Fragment {
    FavoritesViewModel favoritesViewModel;
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private TextView postTitle, postDescription, postUserName, postYummies, postImageErrorMessage;
    private ImageView postImage, postUserImage, postMapBtn, postYummiBtn, postFavoriteBtn;
    private ProgressBar postImageProgressBar, profileImageProgressBar;
    private View view;
    private Post post;
    private String postId;
    private MainActivity mainActivity;
    private DrawableRepository drawableRepository;

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
        this.drawableRepository = RepositoryManager.getInstance().getDrawableRepository(mainActivity);
        this.favoritesViewModel = FavoritesViewModel.getInstance(mainActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);

        profileImageProgressBar = view.findViewById(R.id.profileImageProgressBar);
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

        favoritesViewModel.getAllLinks().observe(mainActivity, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(List<Favorite> favorites) {

                View.OnClickListener clickListener;
                int imageResoucse;
                if (HashSets.convertToLiteWeigtSet(favorites).contains(postId)) {
                    imageResoucse = R.drawable.ic_favorite_svgrepo_com;
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesViewModel.delete(new Favorite(postId));
                        }
                    };
                } else {
                    imageResoucse = R.drawable.ic_favorite_dark;
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesViewModel.insert(new Favorite(postId));
                        }
                    };
                }

                postFavoriteBtn.setImageResource(imageResoucse);
                postFavoriteBtn.setOnClickListener(clickListener);
            }
        });
        return view;
    }

    public View.OnClickListener showPhotoDialogListener(final Drawable drawable) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.main_photo_dialog, null);
                PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                photoView.setImageDrawable(drawable);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        };
    }

    public void updateView() {
        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postYummies.setText(String.valueOf(post.getYummies()));
        postUserName.setText(post.getUserName());

        if (getContext() != null) {
            if (postImageProgressBar.getVisibility() == View.VISIBLE) {
                final String pictureUrl = post.getPicture();
                drawableRepository.getDrawableResource(pictureUrl.hashCode()).observe(mainActivity, new Observer<DrawableResource>() {
                    @Override
                    public void onChanged(DrawableResource drawableResource) {
                        if (drawableResource == null) {
                            Glide.with(getContext())
                                    .load(pictureUrl)
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
                                            drawableRepository.insertDrawable(new DrawableResource(pictureUrl.hashCode(), resource));
                                            postImage.setOnClickListener(showPhotoDialogListener(resource));
                                            return false;
                                        }
                                    }).into(postImage);
                        } else {
                            postImageProgressBar.setVisibility(View.GONE);
                            postImage.setImageDrawable(drawableResource.getDrawable());
                            postImage.setOnClickListener(showPhotoDialogListener(drawableResource.getDrawable()));
                        }
                    }
                });
            }

            if (profileImageProgressBar.getVisibility() == View.VISIBLE) {
                final String profilePictureUrl = post.getUserimg();
                drawableRepository.getDrawableResource(profilePictureUrl.hashCode()).observe(mainActivity, new Observer<DrawableResource>() {
                    @Override
                    public void onChanged(final DrawableResource drawableResource) {
                        if (drawableResource == null) {
                            Glide.with(getContext()).load(post.getUserimg())
                                    .apply(RequestOptions.circleCropTransform())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            drawableRepository.insertDrawable(new DrawableResource(profilePictureUrl.hashCode(), resource));
                                            profileImageProgressBar.setVisibility(View.INVISIBLE);
                                            return false;
                                        }
                                    })
                                    .into(postUserImage);
                        } else {
                            Glide.with(mainActivity).load(drawableResource.getDrawable()).apply(RequestOptions.circleCropTransform()).into(postUserImage);
                            profileImageProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
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
