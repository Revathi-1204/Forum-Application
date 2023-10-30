package com.learning.spring.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private DomainUserService domainUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var domainUserOpt = domainUserService.getByName(username);
        if (domainUserOpt.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Can't find %s in the db", username));
        }
        var domainUser = domainUserOpt.get();
        UserDetails userDetails = User
                .withUsername(domainUser.getName())
                .password(domainUser.getPassword())
                .build();
        return userDetails;
    }
}
