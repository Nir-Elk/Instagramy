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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.helpers.PostAdapterHelper;
import com.instagramy.models.Favorite;
import com.instagramy.models.Post;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;
import com.instagramy.utils.HashSets;
import com.instagramy.view.models.FavoritesViewModel;

import java.util.List;

import static com.instagramy.helpers.PostAdapterHelper.populatePostView;

public class PostFragment extends Fragment {
    FavoritesViewModel favoritesViewModel;
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private TextView postTitle, postDescription, postUserName, postYummies;
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
                            mainActivity.showMessage("Removed from your Manches");
                        }
                    };
                } else {
                    imageResoucse = R.drawable.ic_favorite_dark;
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesViewModel.insert(new Favorite(postId));
                            mainActivity.showMessage("Added to your Manches");

                        }
                    };
                }

                postFavoriteBtn.setImageResource(imageResoucse);
                postFavoriteBtn.setOnClickListener(clickListener);
            }
        });
        return view;
    }

    public void showBigImageDialog(final Drawable drawable) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.main_photo_dialog, null);
        PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
        photoView.setImageDrawable(drawable);
        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    private void updateView() {
        populatePostView(
                mainActivity,
                mainActivity.getApplicationContext(),
                post,
                favoritesViewModel,
                drawableRepository,
                postRepository,
                authRepository,
                postYummies,
                postTitle,
                postUserName,
                postImage,
                postUserImage,
                postYummiBtn,
                postMapBtn,
                postFavoriteBtn,
                profileImageProgressBar,
                postImageProgressBar,
                post.alreadyYummi(authRepository.getCurrentUser().getEmail()),
                postDescription, new PostAdapterHelper.ShowDialog() {
                    @Override
                    public void show(Drawable drawable) {
                        showBigImageDialog(drawable);
                    }
                });
    }

}
