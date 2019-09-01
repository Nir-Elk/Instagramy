package com.instagramy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.adapters.PostAdapter;
import com.instagramy.models.Link;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;
import com.instagramy.view.models.LinkListViewModel;
import com.instagramy.view.models.PostListViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecycleViewFragment extends ActionBarFragment {
    private PostListViewModel postListViewModel;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private LinearLayoutManager linearLayoutManager;
    private LinkListViewModel linkListViewModel;
    static Set<String> favorites = new HashSet<>();

    public RecycleViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void connectPostAdapter(List<Post> postList) {
        postAdapter = new PostAdapter(getActivity(), postList, LinkListViewModel.getInstance((MainActivity) getActivity()));
        postRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    private void updateUserFavoritesLinks() {
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

    @Override
    public void onStart() {
        super.onStart();
        this.postListViewModel.getLiveData().observe(this, new Observer<PostsList>() {
            @Override
            public void onChanged(PostsList posts) {
                PostsList filtered = new PostsList();
                for (Post post : posts) {
                    if (filter(post)) {
                        filtered.add(post);
                    }
                }
                connectPostAdapter(filtered);
                updateUserFavoritesLinks();
            }
        });
    }
}