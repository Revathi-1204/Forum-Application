package com.learning.spring.social.repositories;

import org.springframework.data.repository.CrudRepository;

import com.learning.spring.social.entities.User;


public interface UserRepository extends CrudRepository<User, Integer> {

}
