package com.learning.spring.social.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Post;

public interface PostRepository extends CrudRepository<Post, Integer>{
    
}
