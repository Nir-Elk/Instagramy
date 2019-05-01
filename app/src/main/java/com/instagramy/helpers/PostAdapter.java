package com.instagramy.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.instagramy.models.Post;
import com.instagramy.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_pos_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.postTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.postImage);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle;
        ImageView postImage;
        public MyViewHolder(View itemView){
            super(itemView);
            postTitle = itemView.findViewById(R.id.row_post_title);
            postImage = itemView.findViewById(R.id.row_post_img);
        }
    }
}
