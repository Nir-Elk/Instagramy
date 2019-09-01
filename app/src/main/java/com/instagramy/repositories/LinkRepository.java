package com.instagramy.repositories;

import androidx.lifecycle.LiveData;

import com.instagramy.models.Link;

import java.util.List;

public interface LinkRepository {

    LiveData<List<Link>> getAllLinks();

    void insert(Link link);

    Link get(Link link);

    void remove(Link link);

}
