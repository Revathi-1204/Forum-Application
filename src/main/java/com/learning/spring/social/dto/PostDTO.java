package com.learning.spring.social.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.learning.spring.social.entities.Tag;

import lombok.Data;

@Data
public class PostDTO {
    private int id;
    private String title;
    private String content;
    private UserDTO author;
    private Date createdAt;
    private int likesCount;
    private int commentsCount;
    private Set<Tag> tags;
    private List<CommentDTO> comments;
}
