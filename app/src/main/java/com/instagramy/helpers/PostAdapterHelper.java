package com.instagramy.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.models.DrawableResource;
import com.instagramy.models.Favorite;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.utils.HashSets;
import com.instagramy.view.models.FavoritesViewModel;

import java.util.List;

public class PostAdapterHelper {


    public static void populatePostView(
            final AppCompatActivity activity,
            final Context mContext,
            final Post post,
            final FavoritesViewModel favoritesViewModel,
            final DrawableRepository drawableRepository,
            final PostRepository postRepository,
            final AuthRepository authRepository,
            TextView postYummies,
            TextView postTitle,
            TextView postUserName,
            final ImageView postImage,
            final ImageView postUserImage,
            final ImageView postYummiBtn,
            ImageView postMapBtn,
            final ImageView postFavoriteBtn,
            final ProgressBar postProfileImagePreloader,
            final ProgressBar postImageProgressBar,
            TextView postDescription,
            final ShowDialog showDialog) {
        final Favorite favorite = new Favorite(post.getKey());

        favoritesViewModel.getAllLinks().observe(activity, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(List<Favorite> favorites) {

                final boolean isSavedThisPost = HashSets.convertToLiteWeigtSet(favorites).contains(favorite.getPostId());

                postFavoriteBtn.setImageResource(isSavedThisPost ? R.drawable.ic_favorite_svgrepo_com : R.drawable.ic_favorite_dark);

                postFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSavedThisPost) {
                            favoritesViewModel.delete(favorite);
                        } else {
                            favoritesViewModel.insert(favorite);
                        }
                    }
                });
            }
        });

        if (post.alreadyYummi(authRepository.getCurrentUser().getEmail())) {
            postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
        } else {
            postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
        }
        postUserName.setText(post.getUserName());
        postTitle.setText(post.getTitle());

        final String profileImageUrl = post.getUserimg();

        if (postProfileImagePreloader.getVisibility() == View.VISIBLE) {
            drawableRepository.getDrawableResource(profileImageUrl.hashCode()).observe(activity, new Observer<DrawableResource>() {
                @Override
                public void onChanged(DrawableResource drawableResource) {
                    if (drawableResource == null) {
                        Glide.with(mContext).load(post.getUserimg())
                                .apply(RequestOptions.circleCropTransform())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        postProfileImagePreloader.setVisibility(View.INVISIBLE);
                                        drawableRepository.insertDrawable(new DrawableResource(profileImageUrl.hashCode(), resource));
                                        return false;
                                    }
                                })
                                .into(postUserImage);

                    } else {
                        postProfileImagePreloader.setVisibility(View.INVISIBLE);
                        Glide.with(mContext).load(drawableResource.getDrawable()).apply(RequestOptions.circleCropTransform()).into(postUserImage);
                    }
                }
            });
        }

        postYummies.setText(String.valueOf(post.getYummies()));
        postImageProgressBar.setVisibility(View.VISIBLE);

        final String pictureUrl = post.getPicture();


        if (postImageProgressBar.getVisibility() == View.VISIBLE) {
            drawableRepository.getDrawableResource(pictureUrl.hashCode()).observe(activity, new Observer<DrawableResource>() {
                @Override
                public void onChanged(DrawableResource drawableResource) {
                    if (drawableResource == null) {
                        Glide.with(mContext)
                                .load(pictureUrl)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        postImageProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        postImageProgressBar.setVisibility(View.GONE);
                                        drawableRepository.insertDrawable(new DrawableResource(pictureUrl.hashCode(), resource));
                                        return false;
                                    }
                                }).into(postImage);
                    } else {
                        postImageProgressBar.setVisibility(View.GONE);
                        postImage.setImageDrawable(drawableResource.getDrawable());
                    }
                }
            });

            postYummiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = authRepository.getCurrentUser().getEmail();
                    postRepository.updateYummies(post.getKey(), post.toggleYummi(email));
                    if (post.alreadyYummi(authRepository.getCurrentUser().getEmail())) {
                        postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
                    } else {
                        postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
                    }
                }
            });
        }

        final NavGraphDirections.ActionGlobalPostFragment postAction = NavGraphDirections.actionGlobalPostFragment(post.getKey());
        
        final View.OnClickListener toPostClickListener = Navigation.createNavigateOnClickListener(postAction);
        postTitle.setOnClickListener(toPostClickListener);

        if (showDialog == null) {
            postImage.setOnClickListener(toPostClickListener);
        } else {
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog.show(postImage.getDrawable());
                }
            });
        }

        final NavGraphDirections.ActionGlobalProfileFragment profileAction = NavGraphDirections.actionGlobalProfileFragment(post.getUserId());
        postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));

        final NavGraphDirections.ActionGlobalMapFragment mapAction = NavGraphDirections.actionGlobalMapFragment();
        Post.PostList postList = new Post.PostList();
        postList.add(post);
        mapAction.setPosts(postList);
        postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(mapAction));


        if (postDescription != null) {
            postDescription.setText(post.getDescription());
        }
    }

    public interface ShowDialog {
        void show(Drawable drawable);
    }
}
