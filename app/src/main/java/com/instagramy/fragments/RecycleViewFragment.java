package com.instagramy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.adapters.PostAdapter;
import com.instagramy.models.Link;
import com.instagramy.models.LinkListViewModel;
import com.instagramy.models.Post;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecycleViewFragment extends ActionBarFragment {
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private LinearLayoutManager linearLayoutManager;
    private LinkListViewModel linkListViewModel;
    static Set<String> favorites = new HashSet<>();
    private PostRepository postRepository;

    public RecycleViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postRepository = RepositoryManager.getInstance().getPostRepository();

    }

    boolean filter(Post post) {
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        postRecyclerView = view.findViewById(R.id.postRV);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setHasFixedSize(true);
        linkListViewModel = LinkListViewModel.getInstance((MainActivity) getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        postList = new ArrayList<>();
        postRepository.getPosts().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    try {
                        Post post = postsnap.getValue(Post.class);
                        if (filter(post)) {
                            postList.add(post);
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

        postAdapter = new PostAdapter(getActivity(), postList, LinkListViewModel.getInstance((MainActivity) getActivity()));
        postRecyclerView.setAdapter(postAdapter);


        linkListViewModel.getAllLinks().observe(getActivity(), new Observer<List<Link>>() {
            @Override
            public void onChanged(List<Link> links) {
                favorites = new HashSet<>();
                for (Link link : links) {
                    favorites.add(link.getPostId());
                }
            }
        });
    }

}
