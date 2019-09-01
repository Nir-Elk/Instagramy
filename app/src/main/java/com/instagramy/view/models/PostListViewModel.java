package com.instagramy.view.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.instagramy.models.Post;
import com.instagramy.repositories.PostRepository;
import com.instagramy.repositories.RepositoryManager;

public class PostListViewModel extends ViewModel {
    PostListLiveData postListLiveData;
    private PostRepository postRepository;

    public PostListViewModel() {
        this.postRepository = RepositoryManager.getInstance().getPostRepository();
        this.postListLiveData = new PostListLiveData();
    }

    public LiveData<Post.PostList> getLiveData() {
        return postListLiveData;
    }

    class PostListLiveData extends MutableLiveData<Post.PostList> {

        PostListLiveData() {
            setValue(new Post.PostList());
        }

        @Override
        protected void onActive() {
            super.onActive();

            postRepository.getPosts(new PostRepository.GetAllPostsListener() {
                @Override
                public void onSuccsess(Post.PostList posts) {
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
