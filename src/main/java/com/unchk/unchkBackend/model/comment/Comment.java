package com.unchk.unchkBackend.model.comment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.unchk.unchkBackend.model.course.Course;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String author;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
