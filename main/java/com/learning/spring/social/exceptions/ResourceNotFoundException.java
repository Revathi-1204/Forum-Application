package com.learning.spring.social.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{
  private static final long serialVersionUID = 511507614892486187L;

  public ResourceNotFoundException(String message) {
    super(message);
  }
}