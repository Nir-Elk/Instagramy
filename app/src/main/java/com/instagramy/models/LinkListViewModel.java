package com.instagramy.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.instagramy.Repositories.LinkRepository;
import com.instagramy.utils.LinkDataBase;

import java.util.List;

public class LinkListViewModel extends AndroidViewModel {
    private LiveData<List<Link>> data;
    LinkRepository LinkRepository;


    public LinkListViewModel(Application application) {
        super(application);

    }

    public LiveData<List<Link>> getData() {
        return data;
    }

    public void setData(LiveData<List<Link>> data) {
        this.data = data;
    }
}
