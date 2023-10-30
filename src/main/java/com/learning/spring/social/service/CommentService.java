package com.learning.spring.social.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.learning.spring.social.dto.CommentDTO;
import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.repositories.CommentRepository;

@Component
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDTO> findAllByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        Map<Integer, CommentDTO> commentMap = new HashMap<>();

        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                commentMap.putIfAbsent(comment.getId(), createCommentDTO(comment));
            } else {
                Comment ancestor = findCommonAncestor(comment);
                commentMap.putIfAbsent(ancestor.getId(), createCommentDTO(ancestor));
                commentMap.get(ancestor.getId()).getReplies().add(comment);
            }
        }
        return commentMap.values().stream().toList();
    }

    private CommentDTO createCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUser(comment.getUser());
        commentDTO.setPost(comment.getPost());
        return commentDTO;
    }

    private Comment findCommonAncestor(Comment comment) {
        if (comment.getParent() == null) {
            return comment;
        }
        return findCommonAncestor(comment.getParent());
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> findById(Integer id) {
        return commentRepository.findById(id);
    }
}
