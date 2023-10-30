package com.learning.spring.social.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.spring.social.entities.User;
import com.learning.spring.social.repositories.UserRepository;

@Service
public class DomainUserService {
        private static final String ENCODING_STRATEGY = "{bcrypt}";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    
    public DomainUserService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Optional<User> getByName(String name) {
        return userRepository.findByName(name);
    }

    private String prefixEncodingStrategyAndEncode(String rawString) {
        return ENCODING_STRATEGY + passwordEncoder.encode(rawString);
    }

    public User save(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(prefixEncodingStrategyAndEncode(password));
        return userRepository.save(user);
    }
    public User save(String username, String password, String bio, String dateOfBirth, String email) {
		// TODO Auto-generated method stub
		User user = new User();

		user.setName(username);

		user.setPassword(prefixEncodingStrategyAndEncode(password));

		user.setBio(bio);
		user.setEmail(email);

		user.setDateOfBirth(dateOfBirth);
		return userRepository.save(user);

	}

	public void update(String username, String bio, String dateOfBirth, String email) {
		User user = userRepository.findByEmail(email).get();

		user.setName(username);
		user.setBio(bio);
		user.setDateOfBirth(dateOfBirth);
		userRepository.save(user);
	}

	public void savePic(String name, String path) {
		User user = userRepository.findByName(name).get();
		user.setPic(path);
		userRepository.save(user);
	}


}
