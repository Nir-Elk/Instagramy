package com.instagramy.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.instagramy.helpers.postDao;
import com.instagramy.models.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class InternalDB extends RoomDatabase {

    public abstract postDao postDao();
}
