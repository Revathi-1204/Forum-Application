package com.learning.spring.social.entities;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MutedAuthor {
    public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User mutedAuthor) {
		this.author = mutedAuthor;
	}

	public User getUser() {
		return user;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

	

    // Constructors, getters, and setters
}