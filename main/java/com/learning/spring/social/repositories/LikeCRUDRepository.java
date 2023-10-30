package com.learning.spring.social.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Like;

public interface LikeCRUDRepository extends CrudRepository<Like, Integer> {
    @Query(value = "select count(*) from `likes` where postId = ?1", nativeQuery = true)
    Integer countByPostIdnative(Integer postId);

    @Query(value = "select count(*) from Like l where l.likeId.post.id = ?1")
    Integer countByPostId(Integer postId);
}
