package com.learning.spring.social.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Mutedpost;
import com.learning.spring.social.entities.Post;
import com.learning.spring.social.entities.User;

public interface MutedPostRepository extends CrudRepository<Mutedpost, Integer> {
    @Query(value = "select * from Mutedpost c where post_id = ?1", nativeQuery = true)

    List<Mutedpost> findAllByPostId(Integer postId);

    boolean existsByUserAndPost(User user, Post post);

    List<Mutedpost> findAllByUser(User user);

    @Query("SELECT p FROM Post p WHERE p NOT IN (SELECT mp.post FROM Mutedpost mp WHERE mp.user = :user)")
    List<Post> findAllPostsNotMutedByUser(User user);

    Mutedpost findByUserAndPost(User user, Post post);
}
