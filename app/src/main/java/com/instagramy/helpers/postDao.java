package com.instagramy.helpers;

import androidx.room.Dao;
import androidx.room.Insert;

import com.instagramy.models.Post;

@Dao
public interface postDao {

    @Insert
    public void addPost(Post post);



}
