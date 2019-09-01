package com.instagramy.repositories.impl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.instagramy.dao.LinkDao;
import com.instagramy.models.Link;
import com.instagramy.repositories.LinkRepository;
import com.instagramy.utils.LinkDataBase;

import java.util.List;

public class SqlLiteLinkRepository implements LinkRepository {

    private LinkDao linkDao;
    private LiveData<List<Link>> allLinks;

    public SqlLiteLinkRepository(AppCompatActivity activity) {
        LinkDataBase db = LinkDataBase.getDatabase(activity);
        linkDao = db.linkDao();
        allLinks = linkDao.fetchAllLinks();
    }

    @Override
    public LiveData<List<Link>> getAllLinks() {
        return allLinks;
    }

    @Override
    public void insert(Link link) {
        linkDao.insertLink(link);
    }

    @Override
    public Link get(Link link) {
        return linkDao.getLink(link.getPostId()).getValue();
    }

    @Override
    public void remove(Link link) {
        linkDao.deleteLink(link.getPostId());
    }

}
