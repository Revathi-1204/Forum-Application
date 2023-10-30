package com.learning.spring.social.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.Notification;
import com.learning.spring.social.entities.User;





public interface NotificationRepository extends CrudRepository<Notification, Integer> {

// @Autowired
// private PostRepository postRepository;

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findAll();
    Notification findById(int id);

   // List<Notification> findByPost(postRepository.findById(post.post_id));

    // @Query(value = "select n. from notification n join post p on n.post_id  = p.id where p.author_id =?1;")
    // List<Notification> findByPost(Integer userId);
    
}