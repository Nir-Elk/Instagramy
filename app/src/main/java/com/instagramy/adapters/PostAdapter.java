package com.instagramy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.view.models.FavoritesViewModel;

import java.util.List;

import static com.instagramy.helpers.PostAdapterHelper.populatePostView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context mContext;
    private List<Post> mData;
    public FavoritesViewModel favoritesViewModel;
    public DrawableRepository drawableRepository;
    AppCompatActivity activity;

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
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_pos_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        populatePostView(activity,
                mContext,
                mData.get(position),
                favoritesViewModel,
                drawableRepository,
                postRepository,
                authRepository,
                holder.postYummies,
                holder.postTitle,
                holder.postUserName,
                holder.postImage,
                holder.postUserImage,
                holder.postYummiBtn,
                holder.postMapBtn,
                holder.postFavoriteBtn,
                holder.postProfileImagePreloader,
                holder.postImageProgressBar,
                null,
                null
        );
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

}
