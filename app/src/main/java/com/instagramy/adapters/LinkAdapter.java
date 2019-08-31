package com.instagramy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.instagramy.R;
import com.instagramy.models.Link;

import java.util.List;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.MyViewHolder> {
    private Context mContext;
    private List<Link> mData;


    public LinkAdapter(Context mContext, List<Link> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<Link> getmData() {
        return mData;
    }

    @NonNull
    @Override
    public LinkAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_favorite_item, parent, false);
        return new LinkAdapter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final LinkAdapter.MyViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mData.get(position).getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.linkProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.linkProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.linkImage);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView linkImage;
        ProgressBar linkProgressBar;


        public MyViewHolder(View itemView) {
            super(itemView);
            linkImage = itemView.findViewById(R.id.row_favorite_img);
            linkProgressBar = itemView.findViewById(R.id.row_favorite_progressBar);


        }
    }
}
