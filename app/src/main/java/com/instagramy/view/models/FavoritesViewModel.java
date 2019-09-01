package com.instagramy.view.models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.instagramy.models.Favorite;
import com.instagramy.repositories.FavoriteRepository;
import com.instagramy.repositories.RepositoryManager;

import java.util.List;

public class FavoritesViewModel extends ViewModel {
    private static FavoriteRepository favoriteRepository;
    private static LiveData<List<Favorite>> linkListLiveData;

    public FavoritesViewModel() {
    }

    public static FavoritesViewModel getInstance(AppCompatActivity activity) {
        favoriteRepository = RepositoryManager.getInstance().getFavoriteRepository(activity);
        linkListLiveData = favoriteRepository.getAllLinks();
        return ViewModelProviders.of(activity).get(FavoritesViewModel.class);
    }

    public LiveData<List<Favorite>> getAllLinks() {
        return linkListLiveData;
    }

    public void insert(Favorite favorite) {
        favoriteRepository.insert(favorite);
    }

    public void delete(Favorite favorite) {
        favoriteRepository.remove(favorite);
    }

}
