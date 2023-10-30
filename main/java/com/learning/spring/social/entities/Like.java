package com.learning.spring.social.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "likes")
public class Like {
    
    @EmbeddedId
    private LikeId likeId;
}
