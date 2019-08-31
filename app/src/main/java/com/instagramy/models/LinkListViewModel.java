package com.instagramy.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.instagramy.Repositories.LinkRepository;

import java.util.List;

public class LinkListViewModel extends ViewModel {
    private LiveData<List<Link>> allLinks;
    LinkRepository linkRepository;

    public LinkListViewModel(AppCompatActivity activity) {
        linkRepository = new LinkRepository(activity);
        allLinks = linkRepository.getAllLinks();
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
