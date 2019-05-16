package com.instagramy.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.fragments.MainFragmentDirections;
import com.instagramy.models.Post;
import com.instagramy.R;
import com.instagramy.models.Profile;

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
        Glide.with(mContext).load(mData.get(position).getUserimg()).into(holder.postUserImage);
        String yummies = mData.get(position).getYummies()+" Yummies";
        holder.postYummies.setText(yummies);
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.postImage);
        holder.postYummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Posts").child(mData.get(position).getKey()).child("yummies").setValue(mData.get(position).getYummies()+1);
        }
        });
        final MainFragmentDirections.ActionHomeFragmentToMapFragment action = MainFragmentDirections.actionHomeFragmentToMapFragment(mData.get(position).getKey());
        holder.postMapBtn.setOnClickListener(Navigation.createNavigateOnClickListener(action));
        final MainFragmentDirections.ActionHomeFragmentToPostFragment action2 = MainFragmentDirections.actionHomeFragmentToPostFragment(mData.get(position).getKey());
        holder.postImage.setOnClickListener(Navigation.createNavigateOnClickListener(action2));


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postYummies,postUserName;
        ImageView postImage,postUserImage;
        Button postYummiBtn,postMapBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            postImage = itemView.findViewById(R.id.row_post_img);
            postUserImage = itemView.findViewById(R.id.row_post_userimg);
            postUserName = itemView.findViewById(R.id.row_post_username);
            postYummies = itemView.findViewById(R.id.row_post_yummies);
            postYummiBtn = itemView.findViewById(R.id.row_post_yummies_btn);
            postMapBtn = itemView.findViewById(R.id.row_post_map_btn);
        }
    }
}
