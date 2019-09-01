package com.instagramy.repositories.impl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.instagramy.dao.DrawableDao;
import com.instagramy.models.DrawableResource;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.services.DrawableDatabase;

public class SqlLiteDrawableRepository implements DrawableRepository {

    private DrawableDao drawableDao;

    public SqlLiteDrawableRepository(AppCompatActivity activity) {
        DrawableDatabase db = DrawableDatabase.getDatabase(activity);
        drawableDao = db.drawableDao();
    }

    @Override
    public void insertDrawable(DrawableResource drawableResource) {
        drawableDao.insertDrawable(drawableResource);
    }

    @Override
    public void deleteDrawableResource(DrawableResource drawableResource) {
        drawableDao.deleteDrawableResource(drawableResource);
    }

    @Override
    public LiveData<DrawableResource> getDrawableResource(int key) {
        return drawableDao.getDrawableResource(key);
    }
}
