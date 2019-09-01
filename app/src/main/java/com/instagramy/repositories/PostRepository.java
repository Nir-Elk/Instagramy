package com.instagramy.repositories;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.instagramy.models.Post;
import com.instagramy.models.PostsList;

import java.util.List;

public interface PostRepository {

    String createNewPost();

    Task addPost(Post post);

    DatabaseReference getPost(String id);

    void getPosts(GetAllPostsListener getAllPostsListener);

    void updateYummies(String key, List<String> list);

    Task deletePost(String key);

    UploadTask uploadPhoto(String path, Uri photo);

    Task getDownloadPhotoUrl(String path);

    public interface GetAllPostsListener {
        public void onSuccsess(PostsList posts);
    }

}
