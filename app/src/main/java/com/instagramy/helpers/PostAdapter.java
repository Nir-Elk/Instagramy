package com.instagramy.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.fragments.MainFragmentDirections;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private String email;
    private Context mContext;
    private List<Post> mData;
    private DatabaseReference mDatabaseRef;

    public List<Post> getmData() {
        return mData;
    }

    public PostAdapter(Context mContext, List<Post> mData) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.mContext = mContext;
        this.mData = mData;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference();
        this.email = mAuth.getCurrentUser().getEmail();
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_pos_item,parent,false);

        return new MyViewHolder(row);
    }

    private boolean ifLiked(@NonNull final MyViewHolder holder, final int position){
        return mData.get(position).alreadyYummi(this.email);
    }

    private boolean userAlreadySavedThisPost(@NonNull final MyViewHolder holder, final int position) {
        return false;
    }

    private void removeFromUserSavedPosts(@NonNull final MyViewHolder holder, final int position) {

    }

    private void addToUserSavedPosts(@NonNull final MyViewHolder holder, final int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if(ifLiked(holder,position)) {
            holder.postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
        } else {
            holder.postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
        }
        holder.postUserName.setText(mData.get(position).getUserName());
        holder.postTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getUserimg()).apply(RequestOptions.circleCropTransform()).into(holder.postUserImage);
        holder.postYummies.setText(String.valueOf(mData.get(position).getYummies()));
        holder.postProgressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(mData.get(position).getPicture())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.postProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.postProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.postImage);

        holder.postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(ifLiked(holder,position)) {
                    mDatabaseRef.child("Posts").child(mData.get(position).getKey()).child("yummiesSet").setValue(mData.get(position).removeYumminew(email));
                    holder.postYummiBtn.setImageResource(R.mipmap.not_liked_foreground);
                } else {
                    mDatabaseRef.child("Posts").child(mData.get(position).getKey()).child("yummiesSet").setValue(mData.get(position).addYumminew(email));
                    holder.postYummiBtn.setImageResource(R.mipmap.tongue_foreground);
                }
            }});

        final MainFragmentDirections.ActionHomeFragmentToPostFragment postAction = MainFragmentDirections.actionHomeFragmentToPostFragment(mData.get(position).getKey());
        holder.postTitle.setOnClickListener(Navigation.createNavigateOnClickListener(postAction));
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = LayoutInflater.from(mContext).inflate(R.layout.main_photo_dialog, null);
                PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                photoView.setImageDrawable(holder.postImage.getDrawable());
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        holder.postFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save in sql (internal storage)
                if(userAlreadySavedThisPost(holder, position)) {
                    removeFromUserSavedPosts(holder, position);
                    holder.postFavoriteBtn.setImageResource(R.drawable.ic_favorite_dark);
                } else {
                    addToUserSavedPosts(holder, position);
                    holder.postFavoriteBtn.setImageResource(R.drawable.ic_favorite_svgrepo_com);
                }
            }
        });

        final MainFragmentDirections.ActionHomeFragmentToProfileFragment profileAction = MainFragmentDirections.actionHomeFragmentToProfileFragment(mData.get(position).getUserId());
        holder.postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));

        final NavGraphDirections.ActionGlobalMapFragment mapAction = MainFragmentDirections.actionGlobalMapFragment();
        PostsList postsList = new PostsList();
        postsList.add(mData.get(position));
        mapAction.setPosts(postsList);
        holder.postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(mapAction));

        holder.postUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = LayoutInflater.from(mContext).inflate(R.layout.main_photo_dialog, null);
                PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                photoView.setImageDrawable(holder.postUserImage.getDrawable());
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postYummies,postTitle,postUserName;
        ImageView postImage;
        ImageView postUserImage;
        ImageView postYummiBtn;
        ImageView postMapBtn;
        ImageView postFavoriteBtn;
        ProgressBar postProgressBar;

        public MyViewHolder(View itemView){
            super(itemView);
            postImage = itemView.findViewById(R.id.row_post_img);
            postUserImage = itemView.findViewById(R.id.row_post_userimg);
            postUserName = itemView.findViewById(R.id.row_post_username);
            postTitle = itemView.findViewById(R.id.row_post_title);
            postYummies = itemView.findViewById(R.id.row_post_yummies);
            postYummiBtn = itemView.findViewById(R.id.row_post_yummies_btn);
            postMapBtn = itemView.findViewById(R.id.row_post_map_btn);
            postProgressBar = itemView.findViewById(R.id.row_post_progressBar);
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
}
