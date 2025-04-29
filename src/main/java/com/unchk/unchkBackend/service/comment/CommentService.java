package com.unchk.unchkBackend.service.comment;

import com.unchk.unchkBackend.model.comment.Comment;
import com.unchk.unchkBackend.model.course.Course;
import com.unchk.unchkBackend.repository.comment.CommentRepository;
import com.unchk.unchkBackend.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;

    public Comment addCommentToCourse(Long courseId, String content, String author) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .createdAt(LocalDateTime.now())
                .course(course)
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByCourse(Long courseId) {
        return commentRepository.findByCourseId(courseId);
    }
}
