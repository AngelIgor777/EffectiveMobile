package ru.FirstSecurityApp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.FirstSecurityApp.dtos.CommentDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.models.Task;
import ru.FirstSecurityApp.repositories.CommentRepository;
import ru.FirstSecurityApp.repositories.TaskRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public Comment addComment(Long taskId, Comment comment) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        comment.setTask(task);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Comment not found");
        }
    }

    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    // Преобразование Comment в CommentDTO
    public CommentDTO commentConvertToCommentDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getUsername())
                .taskId(comment.getTask().getId())
                .build();
    }

    @Transactional
    public Page<CommentDTO> getCommentsByTask(Long taskId, int page, int size) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        Page<Comment> commentPage = commentRepository.findByTask(task, PageRequest.of(page, size));
        return commentPage.map(this::commentConvertToCommentDto);
    }


}