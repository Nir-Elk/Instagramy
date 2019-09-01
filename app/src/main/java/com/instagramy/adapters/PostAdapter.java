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
import com.instagramy.models.Link;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.view.models.LinkListViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context mContext;
    private List<Post> mData;
    public LinkListViewModel linkListViewModel;
    Set<String> favorites = new HashSet<>();
    public List<Post> getmData() {
        return mData;
    }

    private AuthRepository authRepository;
    private PostRepository postRepository;

    public PostAdapter(Context mContext, List<Post> mData, LinkListViewModel linkListViewModel) {
        this.mContext = mContext;
        this.mData = mData;
        this.linkListViewModel = linkListViewModel;
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();

        linkListViewModel.getAllLinks().observe((MainActivity) mContext, new Observer<List<Link>>() {
            @Override
            public void onChanged(List<Link> links) {
                favorites = new HashSet<>();
                for (Link link : links) {
                    favorites.add(link.getPostId());
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

    private void addToUserSavedPosts(Link link) {
        if (userAlreadySavedThisPost(link)) {
            linkListViewModel.delete(link);
            favorites.remove(link.getPostId());
            showMessage("Removed from your manches!");
        } else {
            linkListViewModel.insert(link);
            favorites.add(link.getPostId());
            showMessage("Added to your manches!");
        }

        this.notifyDataSetChanged();
    }

    private boolean userAlreadySavedThisPost(Link link) {
        return favorites.contains(link.getPostId());
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
        Glide.with(mContext).load(mData.get(position).getUserimg()).apply(RequestOptions.circleCropTransform()).into(holder.postUserImage);
        holder.postYummies.setText(String.valueOf(mData.get(position).getYummies()));
        holder.postImageProgressBar.setVisibility(View.VISIBLE);
        holder.postImageErrorMessage.setVisibility(View.INVISIBLE);
        Glide.with(mContext)
                .load(mData.get(position).getPicture())
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

                        return false;
                    }
                })
                .into(holder.postImage);

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

        final Link link = new Link(mData.get(position).getKey(), mData.get(position).getPicture());
        holder.postFavoriteBtn.setImageResource(userAlreadySavedThisPost(link) ? R.drawable.ic_favorite_svgrepo_com : R.drawable.ic_favorite_dark);

        holder.postFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToUserSavedPosts(link);
            }
        });

        final MainFragmentDirections.ActionHomeFragmentToProfileFragment profileAction = MainFragmentDirections.actionHomeFragmentToProfileFragment(mData.get(position).getUserId());
        holder.postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        holder.postUserImage.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));

        final NavGraphDirections.ActionGlobalMapFragment mapAction = MainFragmentDirections.actionGlobalMapFragment();
        PostsList postsList = new PostsList();
        postsList.add(mData.get(position));
        mapAction.setPosts(postsList);
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
