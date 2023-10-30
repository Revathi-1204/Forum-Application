package com.learning.spring.social.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.learning.spring.social.entities.Favpost;
import com.learning.spring.social.entities.Mutedpost;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.repositories.FavPostRepository;
import com.learning.spring.social.repositories.MutedPostRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class FavMutePostService {

    private final FavPostRepository favPostRepository;
    private final MutedPostRepository mutedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<Favpost> favpostList;

    public List<Mutedpost> mutedpostList;

    @PostConstruct
    public void init() {

        favpostList = new ArrayList<>();

        mutedpostList = new ArrayList<>();
    }

    @Autowired
    public FavMutePostService(FavPostRepository favPostRepository, MutedPostRepository mutedPostRepository,
            UserRepository userRepository, PostRepository postRepository) {
        this.favPostRepository = favPostRepository;
        this.mutedPostRepository = mutedPostRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public List<Favpost> findAllFavPostsByUser(User user) {
        return favPostRepository.findAllByUser(user);
    }

    public boolean isFavPost(User user, Post post) {
        return favPostRepository.existsByUserAndPost(user, post);
    }

    public void removeFavPost(User user, Post post) {
        Favpost favpost = favPostRepository.findByUserAndPost(user, post);
        if (favpost != null) {
            favPostRepository.delete(favpost);
        }
    }

    public List<Mutedpost> findAllMutedPostsByPostId(Integer postId) {
        return mutedPostRepository.findAllByPostId(postId);
    }

    public boolean isPostMutedByUser(User user, Post post) {
        return mutedPostRepository.existsByUserAndPost(user, post);
    }

    public List<Post> findAllPostsNotMutedByUser(User user) {
        return mutedPostRepository.findAllPostsNotMutedByUser(user);
    }

    public void unmutePost(User user, Post post) {
        Mutedpost mutedpost = mutedPostRepository.findByUserAndPost(user, post);
        if (mutedpost != null) {
            mutedPostRepository.delete(mutedpost);
        }
    }

    public String addFavoritePost(int postId, UserDetails userDetails) {
        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        Optional<Post> post = postRepository.findById(postId);

        if (user.isPresent() && post.isPresent()) {
            // Check if the user and post is there in the opposite table
            if (mutedPostRepository.existsByUserAndPost(user.get(), post.get())) {
                return "redirect:/forum";
            }

            // Avoid duplication
            if (favPostRepository.existsByUserAndPost(user.get(), post.get())) {
                return "redirect:/forum";
            } else {
                Favpost favPost = new Favpost();
                favPost.setPost(post.get());
                favPost.setUser(user.get());
                favPostRepository.save(favPost);

                // Success message
                return "post added to favorites";
            }
        }

        return "redirect:/forum/post/error";
    }

    public String mutePost(int postId, UserDetails userDetails) {
        Optional<User> user = userRepository.findByName(userDetails.getUsername());
        Optional<Post> post = postRepository.findById(postId);

        if (user.isPresent() && post.isPresent()) {
            User postAuthor = post.get().getAuthor();

            // Check if the user and post are in the opposite table
            if (favPostRepository.existsByUserAndPost(user.get(), post.get())) {
                return "redirect:/forum";
            }

            // Avoid duplication
            if (mutedPostRepository.existsByUserAndPost(user.get(), post.get())) {
                return "redirect:/forum";
            }

            // User can't mute their own post
            if (user.get().equals(postAuthor)) {
                return "redirect:/forum";
            }

            Mutedpost mutedPost = new Mutedpost();
            mutedPost.setPost(post.get());
            mutedPost.setUser(user.get());
            mutedPostRepository.save(mutedPost);

            return "post has been muted";
        }

        return "redirect:/forum";
    }

    public String unmutePost(int postId, String commenterName) {
        Optional<User> user = userRepository.findByName(commenterName);
        Optional<Post> post = postRepository.findById(postId);

        if (user.isPresent() && post.isPresent()) {
            if (mutedPostRepository.existsByUserAndPost(user.get(), post.get())) {
                Mutedpost mutePost = mutedPostRepository.findByUserAndPost(user.get(), post.get());
                mutedPostRepository.delete(mutePost);
                return "redirect:/forum/post/mutefeed";
            }
        }

        return "redirect:/forum/post/error";
    }

    public void deleteFavPost(User user, Post post) {
        if (favPostRepository.existsByUserAndPost(user, post)) {
            Favpost favPost = favPostRepository.findByUserAndPost(user, post);
            favPostRepository.delete(favPost);
        }
    }
}
