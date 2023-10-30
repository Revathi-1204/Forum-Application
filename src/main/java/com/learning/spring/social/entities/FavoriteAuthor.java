package com.learning.spring.social.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FavoriteAuthor {

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User favoriteAuthor) {
		this.author = favoriteAuthor;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;


    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "author_id",referencedColumnName = "id")
    private User author;

	
    // Constructors, getters, and setters
}