package com.instagramy.Repositories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.instagramy.dao.LinkDao;
import com.instagramy.models.Link;
import com.instagramy.utils.LinkDataBase;

import java.util.List;

public class LinkRepository {

    private LinkDao linkDao;
    private LiveData<List<Link>> allLinks;

    public LinkRepository(AppCompatActivity activity) {
        LinkDataBase db = LinkDataBase.getDatabase(activity);
        linkDao = db.linkDao();
        allLinks = linkDao.fetchAllLinks();
    }

    public LiveData<List<Link>> getAllLinks() {
        return allLinks;
    }

    public void insert(Link link) {
        linkDao.insertLink(link);
    }

    public Link get(Link link) {
        return linkDao.getLink(link.getPostId()).getValue();
    }

    public void remove(Link link) {

        linkDao.deleteLink(link.getPostId());
    }


}
