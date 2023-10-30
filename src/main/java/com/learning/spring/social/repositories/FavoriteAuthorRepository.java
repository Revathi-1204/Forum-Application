package com.learning.spring.social.repositories;

import com.learning.spring.social.entities.FavoriteAuthor;
import com.learning.spring.social.entities.User;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteAuthorRepository extends JpaRepository<FavoriteAuthor, Integer> {

    List<FavoriteAuthor> findAllByUserId(int userId);

	List<FavoriteAuthor> findByUserAndAuthor(User currentUser, User author);

	
    void deleteByUserAndAuthor(User user, User author);
}