package com.learning.spring.social.bindings;


import lombok.Data;

@Data
public class AddPostForm {
  private String content;
  private String title;
  private int authorId;
}
