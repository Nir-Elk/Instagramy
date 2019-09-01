package com.instagramy.services;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.instagramy.dao.FavoriteDao;
import com.instagramy.models.Favorite;

@Database(entities = {Favorite.class}, version = 16)
public abstract class FavoritesDatabase extends RoomDatabase {
    private final static String DB_NAME = "links.db";

    private static FavoritesDatabase INSTANCE;

    public static FavoritesDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, FavoritesDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();


        }
        return INSTANCE;
    }

    public abstract FavoriteDao linkDao();
}
