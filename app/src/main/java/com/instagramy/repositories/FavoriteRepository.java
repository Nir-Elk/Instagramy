package com.instagramy.repositories;

import androidx.lifecycle.LiveData;

import com.instagramy.models.Favorite;

import java.util.List;

public interface FavoriteRepository {

    LiveData<List<Favorite>> getAllLinks();

    void insert(Favorite favorite);

    void remove(Favorite favorite);
}
