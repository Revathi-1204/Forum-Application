package com.learning.spring.social.dto;

import java.util.ArrayList;
import java.util.List;

import com.learning.spring.social.entities.Comment;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer id;
    private Post post;
    private List<Comment> replies = new ArrayList<>();
    private String content;
    private User user;
}
