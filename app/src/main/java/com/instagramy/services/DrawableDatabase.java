package com.instagramy.services;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.instagramy.dao.DrawableDao;
import com.instagramy.models.DrawableResource;

@Database(entities = {DrawableResource.class}, version = 1)
@TypeConverters({DrawableResource.Converters.class})
public abstract class DrawableDatabase extends RoomDatabase {
    public final static String DRAWABLE_DB_NAME = "links.db";
    private final static String DB_NAME = "links.db";

    private static DrawableDatabase INSTANCE;

    public static DrawableDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, DrawableDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();


        }
        return INSTANCE;
    }

    public abstract DrawableDao drawableDao();
}
