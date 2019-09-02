package com.instagramy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.fragments.MainFragmentDirections;
import com.instagramy.models.DrawableResource;
import com.instagramy.models.Favorite;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.view.models.FavoritesViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context mContext;
    private List<Post> mData;
    public FavoritesViewModel favoritesViewModel;
    public DrawableRepository drawableRepository;
    AppCompatActivity activity;
    Set<String> favorites = new HashSet<>();

    public List<Post> getmData() {
        return mData;
    }

    private AuthRepository authRepository;
    private PostRepository postRepository;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.activity = (MainActivity) mContext;
        this.mData = mData;
        this.favoritesViewModel = FavoritesViewModel.getInstance(activity);
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
        this.drawableRepository = RepositoryManager.getInstance().getDrawableRepository(activity);
        favoritesViewModel.getAllLinks().observe(activity, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(List<Favorite> favorites) {
                PostAdapter.this.favorites = new HashSet<>();
                for (Favorite favorite : favorites) {
                    PostAdapter.this.favorites.add(favorite.getPostId());
                }
            }
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_pos_item, parent, false);
        return new MyViewHolder(row);
    }

    private boolean ifLiked(final int position, String email) {
        return mData.get(position).alreadyYummi(email);
    }

    private void addToUserSavedPosts(Favorite favorite) {
        if (userAlreadySavedThisPost(favorite)) {
            favoritesViewModel.delete(favorite);
            favorites.remove(favorite.getPostId());
            showMessage("Removed from your manches!");
        } else {
            favoritesViewModel.insert(favorite);
            favorites.add(favorite.getPostId());
            showMessage("Added to your manches!");
        }

        this.notifyDataSetChanged();
    }

    private boolean userAlreadySavedThisPost(Favorite favorite) {
        return favorites.contains(favorite.getPostId());
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (ifLiked(position, authRepository.getCurrentUser().getEmail())) {
            holder.postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
        } else {
            holder.postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
        }
        holder.postUserName.setText(mData.get(position).getUserName());
        holder.postTitle.setText(mData.get(position).getTitle());

        final String profileImageUrl = mData.get(position).getUserimg();

        holder.postProfileImagePreloader.setVisibility(View.VISIBLE);
        drawableRepository.getDrawableResource(profileImageUrl.hashCode()).observe(activity, new Observer<DrawableResource>() {
            @Override
            public void onChanged(DrawableResource drawableResource) {
                if (drawableResource == null) {
                    Glide.with(mContext).load(mData.get(position).getUserimg())
                            .apply(RequestOptions.circleCropTransform())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.postProfileImagePreloader.setVisibility(View.INVISIBLE);
                                    drawableRepository.insertDrawable(new DrawableResource(profileImageUrl.hashCode(), resource));
                                    return false;
                                }
                            })
                            .into(holder.postUserImage);

                } else {
                    holder.postProfileImagePreloader.setVisibility(View.INVISIBLE);
                    Glide.with(mContext).load(drawableResource.getDrawable()).apply(RequestOptions.circleCropTransform()).into(holder.postUserImage);
                }
            }
        });


        holder.postYummies.setText(String.valueOf(mData.get(position).getYummies()));
        holder.postImageProgressBar.setVisibility(View.VISIBLE);
        holder.postImageErrorMessage.setVisibility(View.INVISIBLE);

        final String pictureUrl = mData.get(position).getPicture();


        drawableRepository.getDrawableResource(pictureUrl.hashCode()).observe(activity, new Observer<DrawableResource>() {
            @Override
            public void onChanged(DrawableResource drawableResource) {
                if (drawableResource == null) {
                    Glide.with(mContext)
                            .load(pictureUrl)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.postImageProgressBar.setVisibility(View.GONE);
                                    holder.postImageErrorMessage.setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.postImageProgressBar.setVisibility(View.GONE);
                                    drawableRepository.insertDrawable(new DrawableResource(pictureUrl.hashCode(), resource));
                                    return false;
                                }
                            }).into(holder.postImage);
                } else {
                    holder.postImageProgressBar.setVisibility(View.GONE);
                    holder.postImage.setImageDrawable(drawableResource.getDrawable());
                }
            }
        });

        holder.postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = authRepository.getCurrentUser().getEmail();
                postRepository.updateYummies(mData.get(position).getKey(), mData.get(position).toggleYummi(email));
                if (ifLiked(position, email)) {
                    holder.postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
                } else {
                    holder.postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
                }
            }
        });

        final NavGraphDirections.ActionGlobalPostFragment postAction = NavGraphDirections.actionGlobalPostFragment(mData.get(position).getKey());

        final View.OnClickListener toPostClickListener = Navigation.createNavigateOnClickListener(postAction);
        holder.postTitle.setOnClickListener(toPostClickListener);
        holder.postImage.setOnClickListener(toPostClickListener);

        final Favorite favorite = new Favorite(mData.get(position).getKey());
        holder.postFavoriteBtn.setImageResource(userAlreadySavedThisPost(favorite) ? R.drawable.ic_favorite_svgrepo_com : R.drawable.ic_favorite_dark);

        holder.postFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToUserSavedPosts(favorite);
            }
        });

        final MainFragmentDirections.ActionHomeFragmentToProfileFragment profileAction = MainFragmentDirections.actionHomeFragmentToProfileFragment(mData.get(position).getUserId());
        holder.postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        holder.postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));

        final NavGraphDirections.ActionGlobalMapFragment mapAction = MainFragmentDirections.actionGlobalMapFragment();
        Post.PostList postList = new Post.PostList();
        postList.add(mData.get(position));
        mapAction.setPosts(postList);
        holder.postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(mapAction));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView postYummies, postTitle, postUserName;
        ImageView postImage;
        ImageView postUserImage;
        ImageView postYummiBtn;
        ImageView postMapBtn;
        ImageView postFavoriteBtn;
        ProgressBar postProfileImagePreloader;
        ProgressBar postImageProgressBar;
        TextView postImageErrorMessage;

        public MyViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.row_post_img);
            postUserImage = itemView.findViewById(R.id.row_post_userimg);
            postUserName = itemView.findViewById(R.id.row_post_username);
            postTitle = itemView.findViewById(R.id.row_post_title);
            postYummies = itemView.findViewById(R.id.row_post_yummies);
            postYummiBtn = itemView.findViewById(R.id.row_post_yummies_btn);
            postMapBtn = itemView.findViewById(R.id.row_post_map_btn);
            postImageProgressBar = itemView.findViewById(R.id.row_post_progressBar);
            postImageErrorMessage = itemView.findViewById(R.id.row_post_image_error_msg);
            postFavoriteBtn = itemView.findViewById(R.id.row_post_favorite_btn);
            postProfileImagePreloader = itemView.findViewById(R.id.post_profile_image_preloader);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
