package com.learning.spring.social.service;

 


import java.util.Date;

import java.util.List;

import java.util.stream.Collectors;

 

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;

import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.RequestParam;

import com.learning.spring.social.entities.Post;
import com.learning.spring.social.repositories.LikeCRUDRepository;
import com.learning.spring.social.repositories.PostRepository;
 

@Service

@Controller

public class SortingPosts {

	@Autowired
	private LikeCRUDRepository likeCRUDRepository;

	@Autowired
	private PostRepository postRepository;



	//sort posts by likes
	public List<Post> sortPostsByLikes(List<Post> posts) {
        return posts.stream()
                .sorted((post1,post2) -> { 
                	Integer likeCount1 = likeCRUDRepository.countByPostId(post1.getId());
                	Integer likeCount2 = likeCRUDRepository.countByPostId(post2.getId());
                	return likeCount2.compareTo(likeCount1);})
                .collect(Collectors.toList());
    }


	//sort posts by Timestamp
	public List<Post> sortPostsByTimestamp(List<Post> posts) {
		return posts.stream()
            .sorted((post1, post2) -> {
                Long timestamp1 = postRepository.sortPostsByCreatedAt(post1.getId());
                Long timestamp2 = postRepository.sortPostsByCreatedAt(post2.getId());
                return Long.compare(timestamp1, timestamp2);
            })

            .collect(Collectors.toList());

	}

 

	public List<Post> sortPostsByLikes(List<Post> posts, String sortOrder) {
    return posts.stream()
            .sorted((post1, post2) -> {
                Integer likeCount1 = likeCRUDRepository.countByPostId(post1.getId());
                Integer likeCount2 = likeCRUDRepository.countByPostId(post2.getId());
                int comparisonResult = likeCount2.compareTo(likeCount1);

                // Apply sorting order
                if ("desc".equals(sortOrder)) 
                    return comparisonResult; // Descending order
                else 
                    return -comparisonResult; // Ascending order (invert comparison result)
            })

            .collect(Collectors.toList());

}

 

public List<Post> sortPostsByTimestamp(List<Post> posts, String sortOrder) {
    return posts.stream()
            .sorted((post1, post2) -> {
                Long timestamp1 = postRepository.sortPostsByCreatedAt(post1.getId());
                Long timestamp2 = postRepository.sortPostsByCreatedAt(post2.getId());
                int comparisonResult = Long.compare(timestamp1, timestamp2);

 

                // Apply sorting order
                if ("desc".equals(sortOrder)) {
                    return comparisonResult; // Descending order
                } else {
                    return -comparisonResult; // Ascending order (invert comparison result)
                }
            })
            .collect(Collectors.toList());

}
	//Filter posts for a specific number of likes

	public List<Post> filterPostsByMinLikes(List<Post> posts, int minLikes) {
    return posts.stream()
            .filter(post -> likeCRUDRepository.countByPostId(post.getId()) >= minLikes)
            .collect(Collectors.toList());
	}


	//Filter posts for a specific number of comments

//	public List<Post> filterPostsByMinComments(List<Post> posts, int minComments) {

//    return posts.stream()

//            .filter(post -> commentRepository.countByPostId(post.getId()) > minComments)

//            .collect(Collectors.toList());

//	}

//	

	//Filter posts by specific date range

	public List<Post> filterPostsByDateRange(List<Post> posts, 
			@RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) 
	{
	    return posts.stream()
	            .filter(post -> post.getCreatedAt().after(startDate) && post.getCreatedAt().before(endDate))
	            .collect(Collectors.toList());

	}


//	//Filter posts by specific author

//	public List<Post> filterPostsByAuthor(List<Post> posts, String authorName) {

//    return posts.stream()

//            .filter(post -> post.getAuthor().equals(authorName))

//            .collect(Collectors.toList());

//	}

 

 

}
