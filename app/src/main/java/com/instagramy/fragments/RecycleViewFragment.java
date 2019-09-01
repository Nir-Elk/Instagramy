package com.instagramy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.instagramy.R;
import com.instagramy.activities.MainActivity;
import com.instagramy.adapters.PostAdapter;
import com.instagramy.models.Favorite;
import com.instagramy.models.Post;
import com.instagramy.view.models.FavoritesViewModel;
import com.instagramy.view.models.PostListViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecycleViewFragment extends ActionBarFragment {
    private PostListViewModel postListViewModel;
    private FavoritesViewModel favoritesViewModel;

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private LinearLayoutManager linearLayoutManager;
    static Set<String> favorites = new HashSet<>();

    public RecycleViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        this.favoritesViewModel = FavoritesViewModel.getInstance((AppCompatActivity) getActivity());
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
        favoritesViewModel = FavoritesViewModel.getInstance((MainActivity) getActivity());
        return view;
    }

    private void generateAdapter(List<Post> postList) {
        postAdapter = new PostAdapter(getActivity(), postList, FavoritesViewModel.getInstance((MainActivity) getActivity()));
        postRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        this.favoritesViewModel.getAllLinks().removeObservers(this);
        this.favoritesViewModel.getAllLinks().observe(this, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(List<Favorite> favorites) {
                RecycleViewFragment.favorites = convertToLiteWeigtSet(favorites);
                postAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.postListViewModel.getLiveData().observe(this, new Observer<Post.PostList>() {
            @Override
            public void onChanged(Post.PostList posts) {
                Post.PostList filtered = new Post.PostList();
                for (Post post : posts) {
                    if (filter(post)) {
                        filtered.add(post);
                    }
                }
                generateAdapter(filtered);
            }
        });
    }

    Set<String> convertToLiteWeigtSet(List<Favorite> favoriteList) {
        HashSet<String> result = new HashSet<>();
        for (Favorite favorite : favoriteList) {
            result.add(favorite.getPostId());
        }
        return result;
    }
}