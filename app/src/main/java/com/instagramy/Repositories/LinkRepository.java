package com.instagramy.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.instagramy.dao.LinkDao;
import com.instagramy.models.Link;
import com.instagramy.utils.LinkDataBase;

import java.util.List;

public class LinkRepository {

    private LinkDao linkDao;
    private LiveData<List<Link>> allLinks;

    LinkRepository(Application application) {
        LinkDataBase db = LinkDataBase.getDatabase(application);
        linkDao = db.linkDao();
        allLinks = linkDao.fetchAllLinks();
    }

    LiveData<List<Link>> getAllWords() {
        return allLinks;
    }

    public void insert(Link link) {
        new insertAsyncTask(linkDao).execute(link);
    }

    private static class insertAsyncTask extends AsyncTask<Link, Void, Void> {

        private LinkDao mAsyncTaskDao;

        insertAsyncTask(LinkDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Link... params) {
            mAsyncTaskDao.insertLink(params[0]);
            return null;
        }
    }


}
