package com.instagramy.repositories.impl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.instagramy.dao.FavoriteDao;
import com.instagramy.models.Favorite;
import com.instagramy.repositories.FavoriteRepository;
import com.instagramy.services.LocalDatabase;

import java.util.List;

public class SqlLiteFavoriteRepository implements FavoriteRepository {

    private FavoriteDao favoriteDao;

    public SqlLiteFavoriteRepository(AppCompatActivity activity) {
        LocalDatabase db = LocalDatabase.getDatabase(activity);
        favoriteDao = db.linkDao();
    }


    @Override
    public LiveData<List<Favorite>> getAllLinks() {
        return favoriteDao.fetchAllLinks();
    }

    @Override
    public void insert(Favorite favorite) {
        favoriteDao.insertFavorite(favorite);
    }

    @Override
    public void remove(Favorite favorite) {
        favoriteDao.deleteFavorite(favorite.getPostId());
    }

}
