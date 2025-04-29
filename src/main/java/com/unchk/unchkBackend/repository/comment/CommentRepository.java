package com.unchk.unchkBackend.repository.comment;

import com.unchk.unchkBackend.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCourseId(Long courseId);
}
