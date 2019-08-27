package com.instagramy.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.models.Post;

public class PostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String postId;
    FirebaseDatabase database;
    DatabaseReference mDatabaseRef;
    TextView title, description, username, yummies;
    ImageView postImg, userImg;
    Button yummiBtn, mapBtn;
    private View view;
    private Post post;


    private OnFragmentInteractionListener mListener;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this.postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
        this.database = FirebaseDatabase.getInstance();
        this.mDatabaseRef = database.getReference().child("Posts").child(postId);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);


        title = view.findViewById(R.id.post_title);
        description = view.findViewById(R.id.post_description);
        username = view.findViewById(R.id.post_username);
        yummies = view.findViewById(R.id.post_yummies);
        postImg = view.findViewById(R.id.post_img);
        userImg = view.findViewById(R.id.post_userimg);
        yummiBtn = view.findViewById(R.id.post_yummies_btn);
        mapBtn = view.findViewById(R.id.post_map_btn);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
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

        title.setText(post.getTitle());
        description.setText(post.getDescription());
        yummies.setText(post.getYummies());
        yummiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("yummies").setValue(String.valueOf(Integer.parseInt(post.getYummies())+1));
            }
        });

        username.setText(post.getUserName());

        Glide.with(getContext())
                .load(post.getUserimg())
                .apply(RequestOptions.circleCropTransform())
                .into(userImg);

        Glide.with(getContext()).load(post.getPicture()).into(new ImageViewTarget<Drawable>(postImg) {
            @Override
            protected void setResource(@Nullable final Drawable resource) {

                postImg.setImageDrawable(resource);

                postImg.setOnClickListener(new View.OnClickListener() {
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

        final PostFragmentDirections.ActionPostFragmentToProfileFragment profileAction = PostFragmentDirections.actionPostFragmentToProfileFragment(post.getUserId());
        username.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
        userImg.setOnClickListener(Navigation.createNavigateOnClickListener(profileAction));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
