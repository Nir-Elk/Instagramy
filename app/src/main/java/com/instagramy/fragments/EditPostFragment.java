package com.instagramy.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.NavGraphDirections;
import com.instagramy.R;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;
import com.instagramy.repositories.AuthRepository;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;

public class EditPostFragment extends Fragment {
    private PostRepository postRepository;
    private AuthRepository authRepository;
    private EditText postTitle, postDescription;
    private TextView postImageErrorMessage;
    private ImageView postImage;
    private ProgressBar postImageProgressBar;
    private View view;
    private Post post;
    private String postId;


    public EditPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.authRepository = RepositoryManager.getInstance().getAuthRepository();
        postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        postTitle = view.findViewById(R.id.post_edit_title);
        postDescription = view.findViewById(R.id.post_edit_description);
        postImage = view.findViewById(R.id.post_edit_img);
        postImageProgressBar = view.findViewById(R.id.post_edit_progressBar);

        postRepository.getPost(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post = dataSnapshot.getValue(Post.class);
                updateView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    public void updateView() {

        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postImageProgressBar.setVisibility(View.INVISIBLE);
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(post.getPicture())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            postImageProgressBar.setVisibility(View.GONE);
                            postImageErrorMessage.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            postImageProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(new ImageViewTarget<Drawable>(postImage) {
                        @Override
                        protected void setResource(@Nullable final Drawable resource) {

                            postImage.setImageDrawable(resource);

                            postImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                    View mView = getLayoutInflater().inflate(R.layout.main_photo_dialog, null);
                                    PhotoView photoView = mView.findViewById(R.id.mainPhotoView);
                                    photoView.setImageDrawable(resource);
                                    mBuilder.setView(mView);
                                    AlertDialog mDialog = mBuilder.create();
                                    mDialog.show();
                                }
                            });
                        }
                    });
        }




    }

}
