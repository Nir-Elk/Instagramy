package com.instagramy.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instagramy.R;
import com.instagramy.fragments.MainFragmentDirections;
import com.instagramy.models.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;
    FirebaseAuth auth;
    FirebaseDatabase  database;
    DatabaseReference mDatabaseRef;


    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_pos_item,parent,false);

        return new MyViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.postUserName.setText(mData.get(position).getUserName());
        holder.postTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getUserimg()).apply(RequestOptions.circleCropTransform()).into(holder.postUserImage);
        holder.postYummies.setText(String.valueOf(mData.get(position).getYummies()));
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.postImage);
        holder.postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Posts").child(mData.get(position).getKey()).child("yummies").setValue(mData.get(position).addYummi());
        }
        });
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

        final MainFragmentDirections.ActionHomeFragmentToProfileFragment profileAction = MainFragmentDirections.actionHomeFragmentToProfileFragment(mData.get(position).getUserId());
        holder.postUserName.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
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

        public MyViewHolder(View itemView){
            super(itemView);
            postImage = itemView.findViewById(R.id.row_post_img);
            postUserImage = itemView.findViewById(R.id.row_post_userimg);
            postUserName = itemView.findViewById(R.id.row_post_username);
            postTitle = itemView.findViewById(R.id.row_post_title);
            postYummies = itemView.findViewById(R.id.row_post_yummies);
            postYummiBtn = itemView.findViewById(R.id.row_post_yummies_btn);
            postMapBtn = itemView.findViewById(R.id.row_post_map_btn);
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
