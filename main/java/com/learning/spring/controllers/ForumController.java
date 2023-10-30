package com.learning.spring.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.spring.social.bindings.AddCommentForm;
import com.learning.spring.social.bindings.AddPostForm;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.entities.Like;
import com.learning.spring.social.entities.LikeId;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;
import com.learning.spring.social.exceptions.ResourceNotFoundException;
import com.learning.spring.social.repositories.CommentRepository;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;

@Controller
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    private List<User> userList;

    @PostConstruct
    public void init() {
        userList = new ArrayList<>();
    }

    @GetMapping("/post/form")
    public String getPostForm(Model model) {
        model.addAttribute("postForm", new AddPostForm());
        userRepository.findAll().forEach(user -> userList.add(user));
        model.addAttribute("userList", userList);
        model.addAttribute("authorid", 1);
        return "forum/postForm";
    }

    @PostMapping("/post/add")
    public String addNewPost(@ModelAttribute("postForm") AddPostForm postForm, BindingResult bindingResult,
            RedirectAttributes attr) throws ServletException {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getFieldErrors());
            attr.addFlashAttribute("org.springframework.validation.BindingResult.post", bindingResult);
            attr.addFlashAttribute("post", postForm);
            return "redirect:/forum/post/form";
        }
        Optional<User> user = userRepository.findById(postForm.getAuthorId());
        if (user.isEmpty()) {
            throw new ServletException("Something went seriously wrong and we couldn't find the user in the DB");
        }
        Post post = new Post();
        post.setAuthor(user.get());
        post.setContent(postForm.getContent());
        post.setTitle(postForm.getTitle());
        postRepository.save(post);

        return String.format("redirect:/forum/post/%d", post.getId());
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable int id, Model model) throws ResourceNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new ResourceNotFoundException("No post with the requested ID");
        }
        List<Comment> commentList = commentRepository.findAllByPostId(id);

        model.addAttribute("commentList", commentList);
        model.addAttribute("post", post.get());
        model.addAttribute("userList", userList);
        int numLikes = likeCRUDRepository.countByPostId(id);
        model.addAttribute("likeCount", numLikes);
        model.addAttribute("userId", 1);
        model.addAttribute("commentForm", new AddCommentForm());
        return "forum/posts";
    }

    @PostMapping("/post/{id}/like")
    public String postLike(@PathVariable int id, Integer likerId, RedirectAttributes attr) {
        LikeId likeId = new LikeId();
        System.out.println(likerId);
        likeId.setUser(userRepository.findById(likerId).get());
        likeId.setPost(postRepository.findById(id).get());
        Like like = new Like();
        like.setLikeId(likeId);
        likeCRUDRepository.save(like);
        return String.format("redirect:/forum/post/%d", id);
    }

    @PostMapping("/post/{id}/comment")
    public String commentOnPost(@ModelAttribute("commentForm") AddCommentForm commentForm, @PathVariable int id) {
        Optional<User> user = userRepository.findById(commentForm.getUserId());
        Optional<Post> post = postRepository.findById(id);

        if (user.isEmpty() || post.isEmpty()) {
            System.out.println("Something went wrong");
            return String.format("redirect:/forum/post/%d", id);
        }

        Comment comment = new Comment();
        comment.setContent(commentForm.getContent());
        comment.setPost(post.get());
        comment.setUser(user.get());
        commentRepository.save(comment);

        return String.format("redirect:/forum/post/%d", id);
    }
}