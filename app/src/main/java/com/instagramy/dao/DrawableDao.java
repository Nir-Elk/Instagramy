package com.instagramy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.instagramy.models.DrawableResource;

@Dao
public interface DrawableDao {

    @Insert
    void insertDrawable(DrawableResource drawableResource);

    @Delete
    void deleteDrawableResource(DrawableResource drawableResource);

    @Query("SELECT * FROM DrawableResource WHERE `key` =:key")
    LiveData<DrawableResource> getDrawableResource(int key);
}