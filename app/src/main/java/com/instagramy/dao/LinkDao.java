package com.instagramy.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.instagramy.models.Link;

import java.util.List;

@Dao
public interface LinkDao {
    @Insert
    void insertLink(Link... link);

    @Delete
    void deleteLink(Link link);

    @Query("SELECT * FROM Link WHERE id =:id")
    LiveData<Link> getLink(int id);

    @Query("SELECT * FROM Link")
    LiveData<List<Link>> fetchAllLinks();

}