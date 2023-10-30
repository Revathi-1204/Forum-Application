package com.learning.spring.social.entities;

import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String content;

    @CreationTimestamp
    private java.util.Date createdAt;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "authorId", referencedColumnName = "id")
    private User author;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "PostTags", joinColumns = @JoinColumn(name = "postId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagId", referencedColumnName = "id"))
    private Set<Tag> tags;
}
