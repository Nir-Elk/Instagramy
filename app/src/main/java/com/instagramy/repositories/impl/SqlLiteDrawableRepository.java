package com.instagramy.repositories.impl;

import androidx.appcompat.app.AppCompatActivity;

import com.instagramy.dao.DrawableDao;
import com.instagramy.models.DrawableResource;
import com.instagramy.repositories.DrawableRepository;
import com.instagramy.services.LocalDatabase;

public class SqlLiteDrawableRepository implements DrawableRepository {

    private DrawableDao drawableDao;

    public SqlLiteDrawableRepository(AppCompatActivity activity) {
        LocalDatabase db = LocalDatabase.getDatabase(activity);
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
    public DrawableResource getDrawableResource(int key) {
        return drawableDao.getDrawableResource(key);
    }
}
