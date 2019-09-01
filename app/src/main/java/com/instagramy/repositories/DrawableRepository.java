package com.instagramy.repositories;

import androidx.lifecycle.LiveData;

import com.instagramy.models.DrawableResource;

public interface DrawableRepository {

    void insertDrawable(DrawableResource drawableResource);

    void deleteDrawableResource(DrawableResource drawableResource);

    LiveData<DrawableResource> getDrawableResource(int key);
}
