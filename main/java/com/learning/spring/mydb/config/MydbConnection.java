package com.learning.spring.mydb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.learning.spring.mydb.repositories" })
public class MydbConnection {
    @Value("${spring.mydb.datasource.url}")
    private String url;

    @Value("${spring.mydb.datasource.username}")
    private String username;

    @Value("${spring.mydb.datasource.password}")
    private String password;

    @Primary
    @Bean
    DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Primary
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.learning.spring.mydb.entities")
                .persistenceUnit("mydb")
                .build();
    }

    @Primary
    @Bean
    PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean
    JdbcTemplate mydbJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
