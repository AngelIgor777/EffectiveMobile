package ru.FirstSecurityApp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.FirstSecurityApp.dtos.TaskDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.models.Task;
import ru.FirstSecurityApp.repositories.CommentRepository;
import ru.FirstSecurityApp.repositories.PeopleRepository;
import ru.FirstSecurityApp.repositories.TaskRepository;
import ru.FirstSecurityApp.services.TaskService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTask() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTask_Success() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("New Title");

        TaskDTO expectedDTO = new TaskDTO();
        expectedDTO.setId(taskId);
        expectedDTO.setTitle("New Title");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskService.taskConvertToTaskDto(updatedTask)).thenReturn(expectedDTO);

        TaskDTO resultDTO = taskService.updateTask(taskId, updatedTask);

        assertEquals(expectedDTO, resultDTO);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    public void testUpdateTask_TaskNotFound() {
        Long taskId = 1L;
        Task updatedTask = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskId, updatedTask));
    }

    @Test
    public void testGetTasksByAuthor() {
        Long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Task task = new Task();
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);
        TaskDTO expectedDTO = new TaskDTO();

        when(peopleRepository.findById(authorId)).thenReturn(Optional.of(new Person()));
        when(taskRepository.findByAuthor(any(Person.class), eq(pageable))).thenReturn(taskPage);
        when(taskService.taskConvertToTaskDto(task)).thenReturn(expectedDTO);

        Page<TaskDTO> resultPage = taskService.getTasksByAuthor(authorId, 0, 10);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(expectedDTO, resultPage.getContent().get(0));
    }

    @Test
    public void testGetTasksByAuthor_AuthorNotFound() {
        Long authorId = 1L;
        when(peopleRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTasksByAuthor(authorId, 0, 10));
    }

    @Test
    public void testUpdateTaskStatus_Success() {
        Long taskId = 1L;
        String newStatus = "In Progress";
        String currentUsername = "user1";

        Task task = new Task();
        task.setId(taskId);
        task.setStatus("Pending");
        Person executor = new Person();
        executor.setUsername(currentUsername);
        task.setExecutor(executor);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setStatus(newStatus);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(updatedTask);
        when(taskService.taskConvertToTaskDto(updatedTask)).thenReturn(new TaskDTO());

        TaskDTO resultDTO = taskService.updateTaskStatus(taskId, newStatus, currentUsername);

        assertEquals(newStatus, resultDTO.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTaskStatus_AccessDenied() {
        Long taskId = 1L;
        String newStatus = "In Progress";
        String currentUsername = "user1";

        Task task = new Task();
        task.setId(taskId);
        task.setStatus("Pending");
        Person executor = new Person();
        executor.setUsername("differentUser");
        task.setExecutor(executor);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> taskService.updateTaskStatus(taskId, newStatus, currentUsername));
    }

    @Test
    public void testDeleteTask_Success() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    public void testDeleteTask_TaskNotFound() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));
    }

    @Test
    public void testAddComment() {
        Long taskId = 1L;
        String commentText = "New comment";
        String currentUsername = "user1";

        Task task = new Task();
        Person author = new Person();
        author.setUsername(currentUsername);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(peopleRepository.findByUsername(currentUsername)).thenReturn(Optional.of(author));

        taskService.addComment(taskId, commentText, currentUsername);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testTaskConvertToTaskDto() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setStatus("Pending");
        task.setPriority("High");
        task.setAuthor(new Person());
        task.setExecutor(new Person());

        TaskDTO taskDTO = taskService.taskConvertToTaskDto(task);

        assertNotNull(taskDTO);
        assertEquals(task.getId(), taskDTO.getId());
        assertEquals(task.getTitle(), taskDTO.getTitle());
        assertEquals(task.getDescription(), taskDTO.getDescription());
        assertEquals(task.getStatus(), taskDTO.getStatus());
        assertEquals(task.getPriority(), taskDTO.getPriority());
        assertEquals(task.getAuthor().getId(), taskDTO.getAuthorId());
        assertEquals(task.getExecutor().getId(), taskDTO.getExecutorId());
    }
}
