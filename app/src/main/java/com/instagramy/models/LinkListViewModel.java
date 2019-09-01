package com.instagramy.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.instagramy.repositories.LinkRepository;
import com.instagramy.repositories.RepositoryManager;

import java.util.List;

public class LinkListViewModel extends ViewModel {
    private LiveData<List<Link>> allLinks;
    LinkRepository linkRepository;
    static LinkListViewModel instance;

    private LinkListViewModel(AppCompatActivity activity) {
        linkRepository = RepositoryManager.getInstance().getLinkRepository(activity);
        allLinks = linkRepository.getAllLinks();
    }

    public static LinkListViewModel getInstance(AppCompatActivity activity) {
        if (instance == null) {
            return new LinkListViewModel(activity);
        }
        return instance;
    }

    public LiveData<List<Link>> getAllLinks() {
        return allLinks;
    }

    public void allLinks(LiveData<List<Link>> allLinks) {
        this.allLinks = allLinks;
    }

    public Link get(Link link) {
        return linkRepository.get(link);
    }

    public void insert(Link link) {
        linkRepository.insert(link);
    }

    public void delete(Link link) {
        linkRepository.remove(link);
    }
}
