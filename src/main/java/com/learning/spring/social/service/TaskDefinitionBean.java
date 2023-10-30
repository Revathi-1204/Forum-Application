package com.learning.spring.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.spring.social.bindings.AddPostForm;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.repositories.PostRepository;

@Service
public class TaskDefinitionBean implements Runnable {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    private Integer id;
    private User user;
    private AddPostForm postForm;
    private Post post;

    public TaskDefinitionBean() {
    }

    public TaskDefinitionBean(Post post, PostRepository postRepository,User user,AddPostForm postForm,NotificationService notificationService) {
        this.post = post;
        this.postRepository = postRepository;
        this.user=user;
        this.postForm=postForm;
        this.notificationService=notificationService;
    }

    public TaskDefinitionBean(Integer id, PostService postService) {
        this.id = id;
        this.postService = postService;
    }

    @Override
    public void run() {
        if (post != null) {
            postRepository.save(post);
            notificationService.createNotification(user, post, "POST", "You added a post (" + postForm.getTitle() + ").");
        }

        // if (id != null) {
        //     postService.deleteLikeAndComment(id);
        //     postService.deletePostById(id);
        //notificationService.createNotification(user, post, "POST", "You added a post (" + postForm.getTitle() + ").");
        // }
    }
}