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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.instagramy.utils.HashSets.convertToLiteWeigtSet;

public class RecycleViewFragment extends ActionBarFragment {
    static Set<String> favorites = new HashSet<>();
    private PostListViewModel postListViewModel;
    private FavoritesViewModel favoritesViewModel;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private LinearLayoutManager linearLayoutManager;

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
        postAdapter = new PostAdapter(getActivity(), postList);
        postRecyclerView.setAdapter(postAdapter);
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
        generateAdapter(new LinkedList<Post>());
        this.postListViewModel.getLiveData().observe(this, new Observer<Post.PostList>() {
            @Override
            public void onChanged(Post.PostList posts) {
                postAdapter.getmData().clear();
                for (Post post : posts) {
                    if (filter(post)) {
                        postAdapter.getmData().add(post);
                    }
                }
                Collections.reverse(postAdapter.getmData());
                postAdapter.notifyDataSetChanged();
            }
        });
    }

}