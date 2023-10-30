package com.learning.spring.social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.learning.spring.social.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(customUserDetailsService);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/forum/register", "/error", "/css/**", "/js/**", "/forum","/classroom/**").permitAll()
            .anyRequest().authenticated())
            .formLogin((login) -> login.loginProcessingUrl("/login").defaultSuccessUrl("/forum", true))
            .logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/forum"));
        
        return http.build();
    }

}