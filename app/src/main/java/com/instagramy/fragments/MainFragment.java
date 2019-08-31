package com.instagramy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.adapters.PostAdapter;
import com.instagramy.models.LinkListViewModel;
import com.instagramy.models.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Post> postList;
    private LinearLayoutManager linearLayoutManager;
    private LinkListViewModel linkListViewModel;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        linkListViewModel = new LinkListViewModel((MainActivity) getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().show();
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        postRecyclerView = fragmentView.findViewById(R.id.postRV);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setSelectedItemBottomNavigation(R.id.nav_home);
    }

    @Override
    public void onStart() {
        super.onStart();
        postList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Collections.reverse(postList);
                int i = 0;
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    try {
                        Post post = postsnap.getValue(Post.class);
                        if (i > postList.size() - 1)
                            postList.add(post);
                        else
                            postList.set(i, post);

                        i++;
                    } catch (Exception ignored) {
                    }
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        postAdapter = new PostAdapter(getActivity(), postList, linkListViewModel);
        postRecyclerView.setAdapter(postAdapter);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
