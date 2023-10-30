package com.learning.spring.social.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Comment;

public interface CommentRepository extends CrudRepository<Comment, Integer>{
    @Query(value = "select * from comments c where postId = ?1", nativeQuery = true)
    List<Comment> findAllByPostId(Integer postId);

    @Query(value = "select count(*) from comments c where postId = ?1", nativeQuery = true)
    int countByPostId(Integer postId);
}
