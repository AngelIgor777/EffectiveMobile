package ru.FirstSecurityApp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.FirstSecurityApp.dtos.CommentDTO;
import ru.FirstSecurityApp.dtos.TaskDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.models.Person;

import ru.FirstSecurityApp.models.Task;
import ru.FirstSecurityApp.repositories.CommentRepository;
import ru.FirstSecurityApp.repositories.PeopleRepository;
import ru.FirstSecurityApp.repositories.TaskRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final PeopleRepository peopleRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }


    @Transactional
    public TaskDTO updateTask(Long id, Task task) {
        // Проверка существования задачи
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Обновление существующей задачи с данными из переданного Task
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());

        // Обновление связанного автора и исполнителя, если это требуется
        if (task.getAuthor() != null) {
            Person author = peopleRepository.findById(task.getAuthor().getId()).orElseThrow(() -> new EntityNotFoundException("Author not found"));
            existingTask.setAuthor(author);
        }
        if (task.getExecutor() != null) {
            Person executor = peopleRepository.findById(task.getExecutor().getId()).orElseThrow(() -> new EntityNotFoundException("Executor not found"));
            existingTask.setExecutor(executor);
        }

        // Сохранение обновленной задачи
        Task updatedTask = taskRepository.save(existingTask);

        // Преобразование в TaskDTO
        return taskConvertToTaskDto(updatedTask);
    }


    @Transactional
    public Page<TaskDTO> getTasksByAuthor(Long authorId, int page, int size) {
        Person author = peopleRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        Page<Task> taskPage = taskRepository.findByAuthor(author, PageRequest.of(page, size));
        return taskPage.map(this::taskConvertToTaskDto);
    }

    @Transactional
    public Page<TaskDTO> getTasksByExecutor(Long executorId, int page, int size) {
        Person executor = peopleRepository.findById(executorId)
                .orElseThrow(() -> new EntityNotFoundException("Executor not found"));
        Page<Task> taskPage = taskRepository.findByExecutor(executor, PageRequest.of(page, size));
        return taskPage.map(this::taskConvertToTaskDto);
    }



    @Transactional
    public TaskDTO updateTaskStatus(Long id, String status, String currentUsername) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        if (task.getExecutor().getUsername().equals(currentUsername)) {
            task.setStatus(status);
            Task savedTask = taskRepository.save(task);
            return taskConvertToTaskDto(savedTask);
        } else {
            throw new RuntimeException("Access denied: You are not the executor of this task");
        }
    }


    @Transactional
    public TaskDTO updateTaskPriority(Long id, String priority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setPriority(priority);
        return taskConvertToTaskDto(taskRepository.save(task));
    }

    // Добавление комментария к задаче
    @Transactional
    public void addComment(Long taskId, String commentText, String currentUsername) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        Person author = peopleRepository.findByUsername(currentUsername).orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = Comment.builder().task(task).content(commentText).author(author).build();
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    public TaskDTO taskConvertToTaskDto(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle()) // Исправлено на getTitle
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorId(task.getAuthor().getId())
                .executorId(task.getExecutor() != null ? task.getExecutor().getId() : null)
                .build();
    }


    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<TaskDTO> getAllTasks() {
        List<Task> taskList = taskRepository.findAll();
        return taskList.stream().map(this::taskConvertToTaskDto).collect(Collectors.toList());
    }


}
