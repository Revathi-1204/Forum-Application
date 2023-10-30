package com.learning.spring.social.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.Tag;

public interface TagRepository extends CrudRepository<Tag, Integer> {
    @Query(value = "SELECT * FROM tags t WHERE t.name = ?1 LIMIT 1", nativeQuery = true)
    Tag findByName(String name);


    @Query(value="SELECT t FROM Tag t JOIN t.posts p WHERE p = :post")
    Set<Tag> findByPost(Post post);
}
