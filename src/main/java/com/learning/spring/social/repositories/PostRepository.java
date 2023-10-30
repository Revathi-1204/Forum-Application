package com.learning.spring.social.repositories;

import java.util.Date;
import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.learning.spring.social.entities.Post;

public interface PostRepository extends CrudRepository<Post, Integer> {

    List<Post> findAllByAuthorId(Integer authorId);
    @Query(value = "SELECT * FROM posts p WHERE LOWER(CONCAT(p.title,p.content)) LIKE %?1%", nativeQuery = true)
    List<Post> findAllByPattern(String pattern);

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "JOIN p.tags t " +
            "WHERE t.name = ?1")
    List<Post> findPostsByTagName(String tag);

    @Query(value = "SELECT p FROM Post p WHERE p.author.name = ?1")
    List<Post> findPostsByUser(String username);

    @Query("Select EXTRACT(EPOCH from p.createdAt) from Post p where p.id = :post_id")
	Long sortPostsByCreatedAt(@Param("post_id")Integer post_id);

	List<Post> findByCreatedAtBetween(Date startDate, Date endDate);

 

	@Query("SELECT p FROM Post p WHERE p.id = :post_id AND date(p.createdAt) BETWEEN :startDate AND :endDate")
    List<Post> findPostsByCreatedAtAndDateRange(@Param("post_id") Integer post_id,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);
}
