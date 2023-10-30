package com.learning.spring.social.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.learning.spring.social.dto.CommentDTO;
import com.learning.spring.social.dto.PostDTO;
import com.learning.spring.social.dto.UserDTO;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.Tag;
import com.learning.spring.social.repositories.CommentRepository;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
import com.learning.spring.social.repositories.TagRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeCRUDRepository likeCRUDRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentService commentService;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public List<PostDTO> findAll() {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Post> posts = (List<Post>) postRepository.findAll();

        for (Post post : posts) {
            PostDTO postDTO = createPostDTO(post);
            postDTOs.add(postDTO);
        }

        return postDTOs;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public List<PostDTO> findByPattern(String pattern) {
        List<PostDTO> postDTOs = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        switch (pattern.substring(0, 1)) {
            case "#":
                posts = postRepository.findPostsByTagName(pattern.substring(1));
                break;
            case "@":
                posts = postRepository.findPostsByUser(pattern.substring(1));
                break;
            default:
                posts = postRepository.findAllByPattern(pattern);
                break;
        }

        for (Post post : posts) {
            PostDTO postDTO = createPostDTO(post);
            postDTOs.add(postDTO);
        }

        return postDTOs;
    }

    private PostDTO createPostDTO(Post post) {
        List<CommentDTO> commentDTOs = commentService.findAllByPostId(post.getId());
        PostDTO postDTO = new PostDTO();
        UserDTO authorDTO = new UserDTO();
        authorDTO.setId(post.getAuthor().getId());
        authorDTO.setName(post.getAuthor().getName());
        authorDTO.setSymbol(post.getAuthor().getName().substring(0, 1));
        int likesCount = likeCRUDRepository.countByPostId(post.getId());
        int commentsCount = commentRepository.countByPostId(post.getId());
        Set<Tag> tags = tagRepository.findByPost(post);
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setAuthor(authorDTO);
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setLikesCount(likesCount);
        postDTO.setCommentsCount(commentsCount);
        postDTO.setComments(commentDTOs);
        postDTO.setTags(tags);
        return postDTO;
    }

    public void save(Post post) {
        postRepository.save(post);
    }
}
