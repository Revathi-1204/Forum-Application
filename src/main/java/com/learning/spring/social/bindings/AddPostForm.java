package com.learning.spring.social.bindings;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AddPostForm {
  private String content;
  private String title;
  private String tags;
  private LocalDateTime dateTime;
}
