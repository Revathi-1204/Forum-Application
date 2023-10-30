package com.learning.spring.social.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Favpost;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;

public interface FavPostRepository extends CrudRepository<Favpost, Integer> {

    List<Favpost> findAllByUser(User user);

    boolean existsByUserAndPost(User user, Post post);

    Favpost findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

}
