package com.instagramy.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.instagramy.dao.LinkDao;
import com.instagramy.models.Link;

@Database(entities = {Link.class}, version = 105)
public abstract class LinkDataBase extends RoomDatabase {

    public final static String LINK_DATA_BASE_NAME = "links.db";
    public abstract LinkDao linkDao();

    private static LinkDataBase INSTANCE;

    public static LinkDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, LinkDataBase.class, LINK_DATA_BASE_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();


        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
