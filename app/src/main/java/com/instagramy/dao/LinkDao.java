package com.instagramy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.instagramy.models.Link;

import java.util.List;

@Dao
public interface LinkDao {
    @Insert
    void insertLink(Link... link);

    @Query("DELETE FROM Link WHERE postId =:postId")
    void deleteLink(String postId);

    @Query("SELECT * FROM Link WHERE postId =:postId")
    LiveData<Link> getLink(String postId);

    @Query("SELECT * FROM Link")
    LiveData<List<Link>> fetchAllLinks();

}