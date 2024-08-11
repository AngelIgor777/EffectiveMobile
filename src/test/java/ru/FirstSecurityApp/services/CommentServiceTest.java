package ru.FirstSecurityApp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.FirstSecurityApp.dtos.CommentDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.models.Task;
import ru.FirstSecurityApp.repositories.CommentRepository;
import ru.FirstSecurityApp.repositories.TaskRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddComment_Success() {
        Long taskId = 1L;
        Comment comment = new Comment();
        comment.setContent("Test Comment");

        Task task = new Task();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment savedComment = commentService.addComment(taskId, comment);

        assertNotNull(savedComment);
        assertEquals(comment.getContent(), savedComment.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void testAddComment_TaskNotFound() {
        Long taskId = 1L;
        Comment comment = new Comment();
        comment.setContent("Test Comment");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.addComment(taskId, comment));
    }

    @Test
    public void testDeleteComment_Success() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    public void testDeleteComment_CommentNotFound() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(commentId));
    }

    @Test
    public void testGetCommentsByTaskId() {
        Long taskId = 1L;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");

        when(commentRepository.findByTaskId(taskId)).thenReturn(Collections.singletonList(comment));

        List<Comment> comments = commentService.getCommentsByTaskId(taskId);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Test Comment", comments.get(0).getContent());
    }

    @Test
    public void testGetCommentsByTask_Success() {
        Long taskId = 1L;
        int page = 0;
        int size = 10;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");

        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment), PageRequest.of(page, size), 1);
        CommentDTO commentDTO = CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getUsername())
                .taskId(comment.getTask().getId())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new Task()));
        when(commentRepository.findByTask(any(Task.class), any(PageRequest.class))).thenReturn(commentPage);
        when(commentService.commentConvertToCommentDto(comment)).thenReturn(commentDTO);

        Page<CommentDTO> resultPage = commentService.getCommentsByTask(taskId, page, size);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(commentDTO, resultPage.getContent().get(0));
    }

    @Test
    public void testGetCommentsByTask_TaskNotFound() {
        Long taskId = 1L;
        int page = 0;
        int size = 10;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentsByTask(taskId, page, size));
    }

    @Test
    public void testCommentConvertToCommentDto() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        Person author = new Person();
        author.setId(2L);
        author.setUsername("AuthorName");
        comment.setAuthor(author);
        Task task = new Task();
        task.setId(3L);
        comment.setTask(task);

        CommentDTO commentDTO = commentService.commentConvertToCommentDto(comment);

        assertNotNull(commentDTO);
        assertEquals(comment.getId(), commentDTO.getId());
        assertEquals(comment.getContent(), commentDTO.getContent());
        assertEquals(author.getId(), commentDTO.getAuthorId());
        assertEquals(author.getUsername(), commentDTO.getAuthorName());
        assertEquals(task.getId(), commentDTO.getTaskId());
    }
}
