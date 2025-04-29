package com.unchk.unchkBackend.controller.comment;

import com.unchk.unchkBackend.model.comment.Comment;
import com.unchk.unchkBackend.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{courseId}")
    public Comment addComment(@PathVariable Long courseId,
                              @RequestParam String content,
                              @RequestParam String author) {
        return commentService.addCommentToCourse(courseId, content, author);
    }

    @GetMapping("/{courseId}")
    public List<Comment> getComments(@PathVariable Long courseId) {
        return commentService.getCommentsByCourse(courseId);
    }
}
