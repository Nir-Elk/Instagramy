package com.instagramy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.adapters.PostAdapter;
import com.instagramy.models.Link;
import com.instagramy.models.LinkListViewModel;
import com.instagramy.models.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Post> postList;
    private LinearLayoutManager linearLayoutManager;
    private LinkListViewModel linkListViewModel;
    Set<String> favorites = new HashSet<>();

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linkListViewModel = LinkListViewModel.getInstance((MainActivity) getActivity());

        linkListViewModel.getAllLinks().observe(getActivity(), new Observer<List<Link>>() {
            @Override
            public void onChanged(List<Link> links) {
                favorites = new HashSet<>();
                for (Link link: links) {
                    favorites.add(link.getPostId());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        postRecyclerView = view.findViewById(R.id.postRV);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setSelectedItemBottomNavigation(R.id.nav_favorites);
    }

    boolean postSavedAsFavorite(Post post) {
        return favorites.contains(post.getKey());
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
                        if (postSavedAsFavorite(post)) {
                            if (i > postList.size() - 1) {
                                postList.add(post);
                            } else {
                                postList.set(i, post);
                            }
                            i++;
                        }
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
        postAdapter = new PostAdapter(getActivity(), postList, LinkListViewModel.getInstance((MainActivity)getActivity()));
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
