package com.learning.spring.models;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Component
public class Student {
    private int id;
    private int rank;
    private String name;
    @Min(value = 0)
    private int score;
}
