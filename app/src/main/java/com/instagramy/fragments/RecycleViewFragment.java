package com.instagramy.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.instagramy.models.Link;
import com.instagramy.models.LinkListViewModel;
import com.instagramy.models.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecycleViewFragment extends Fragment {
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Post> postList;
    private LinearLayoutManager linearLayoutManager;
    private LinkListViewModel linkListViewModel;
    static Set<String> favorites = new HashSet<>();
    public ActionBar actionBar;

    public RecycleViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    boolean filter(Post post) {
        return true;
    }


    public View onCreateView(View view, String title) {
        actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        postRecyclerView = view.findViewById(R.id.postRV);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.setHasFixedSize(true);
        linkListViewModel = LinkListViewModel.getInstance((MainActivity) getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        actionBar.setTitle(title);
        return view;
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
                        if (filter(post)) {
                            if (i > postList.size() - 1) {
                                postList.add(post);
                            } else {
                                postList.set(i, post);
                            }
                            i++;
                        }
                    } catch (Exception ignored) { }
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
