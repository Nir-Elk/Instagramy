package com.instagramy.repositories;

import com.instagramy.models.DrawableResource;

public interface DrawableRepository {

    void insertDrawable(DrawableResource drawableResource);

    void deleteDrawableResource(DrawableResource drawableResource);

    DrawableResource getDrawableResource(int key);
}
