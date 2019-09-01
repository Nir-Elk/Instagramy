package com.instagramy.services;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.instagramy.dao.DrawableDao;
import com.instagramy.dao.FavoriteDao;
import com.instagramy.models.DrawableResource;
import com.instagramy.models.Favorite;

@Database(entities = {Favorite.class, DrawableResource.class}, version = 1)
@TypeConverters({DrawableResource.Converters.class})
public abstract class LocalDatabase extends RoomDatabase {
    private final static String DB_NAME = "local.db";

    private static LocalDatabase INSTANCE;

    public static LocalDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, LocalDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();


        }
        return INSTANCE;
    }

    public abstract FavoriteDao linkDao();

    public abstract DrawableDao drawableDao();
}
