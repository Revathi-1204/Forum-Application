package com.learning.spring.social.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "socialEntityManagerFactory", transactionManagerRef = "SocialTransactionManager", basePackages = {
        "com.learning.spring.social.repositories" })
public class SocialDbConnection {
    @Value("${spring.social.datasource.url}")
    private String url;

    @Value("${spring.social.datasource.username}")
    private String username;

    @Value("${spring.social.datasource.password}")
    private String password;

    @Bean(name="socialDataSource")
    DataSource socialDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name="socialEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean SocialEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("socialDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.learning.spring.social.entities")
                .persistenceUnit("social")
                .build();
    }

    @Bean(name="SocialTransactionManager")
    PlatformTransactionManager SocialTransactionManager(
            @Qualifier("socialEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name="socialJdbcTemplate")
    JdbcTemplate socialJdbcTemplate(@Qualifier("socialDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
