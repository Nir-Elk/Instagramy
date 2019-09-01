package com.instagramy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.instagramy.models.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insertFavorite(Favorite... favorite);

    @Query("DELETE FROM Favorite WHERE postId =:postId")
    void deleteFavorite(String postId);

    @Query("SELECT * FROM Favorite")
    LiveData<List<Favorite>> fetchAllLinks();

}