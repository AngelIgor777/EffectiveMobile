package ru.FirstSecurityApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.dtos.CommentDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/tasks/{taskId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long taskId, @RequestBody Comment comment) {
        Comment saveComment = commentService.addComment(taskId, comment);
        System.out.println(comment.getAuthor().getUsername());
        return ResponseEntity.ok(commentService.commentConvertToCommentDto(saveComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CommentDTO> comments = commentService.getCommentsByTask(taskId, page, size);
        return ResponseEntity.ok(comments);
    }
}
