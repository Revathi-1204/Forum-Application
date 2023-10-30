package com.learning.spring.social.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    public Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    Integer countByName(String name);

    @Query(value = "select * from user where name = ?1", nativeQuery = true)

    User findBySomeConstraintSpringCantParse(String name);

}
