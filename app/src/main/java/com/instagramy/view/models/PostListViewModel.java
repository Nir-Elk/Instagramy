package com.instagramy.view.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.instagramy.models.PostsList;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;

public class PostListViewModel extends ViewModel {
    PostListLiveData postListLiveData;
    private PostRepository postRepository;

    public PostListViewModel() {
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.postListLiveData = new PostListLiveData();
    }

    public LiveData<PostsList> getLiveData() {
        return postListLiveData;
    }

    class PostListLiveData extends MutableLiveData<PostsList> {

        PostListLiveData() {
            setValue(new PostsList());
        }

        @Override
        protected void onActive() {
            super.onActive();

            postRepository.getPosts(new PostRepository.GetAllPostsListener() {
                @Override
                public void onSuccsess(PostsList posts) {
                    setValue(posts);
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }
    }

}
