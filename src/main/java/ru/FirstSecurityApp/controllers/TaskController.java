package ru.FirstSecurityApp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.dtos.TaskDTO;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.models.Task;
import ru.FirstSecurityApp.repositories.PeopleRepository;
import ru.FirstSecurityApp.services.TaskService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations related to tasks")
public class TaskController {

    private final TaskService taskService;
    private final PeopleRepository peopleRepository;


    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve a list of all tasks")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        // Проверка на наличие автора
        Optional<Person> author = peopleRepository.findById(task.getAuthor().getId());
        // Проверка на наличие исполнителя (если он указан)
        Optional<Person> executor = task.getExecutor() != null ? peopleRepository.findById(task.getExecutor().getId()) : Optional.empty();
        // Если автор не найден, возвращаем ошибку
        if (author.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        // Сохранение задачи
        Task savedTask = taskService.createTask(task);
        // Преобразование сохраненной задачи в TaskDTO и возврат ответа
        return ResponseEntity.ok(taskService.taskConvertToTaskDto(savedTask));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> addComment(@PathVariable Long id, @RequestParam String commentText, @RequestParam String currentUsername) {
        taskService.addComment(id, commentText, currentUsername);
        return ResponseEntity.ok().build();
    }

    // Метод для обновления важности задачи
    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskDTO> updateTaskPriority(@PathVariable Long id, @RequestParam String priority) {
        try {
            TaskDTO updatedTask = taskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        Task task = taskService.getTask(id);
        return ResponseEntity.ok(taskService.taskConvertToTaskDto(task));
    }

    // Изменение статуса задачи (только для исполнителя)
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        TaskDTO updatedTask = taskService.updateTaskStatus(id, status, currentUsername);
        return ResponseEntity.ok(updatedTask);
    }


    @GetMapping("/executor/{executorId}")
    public ResponseEntity<Page<TaskDTO>> getTasksByExecutor(
            @PathVariable Long executorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TaskDTO> tasks = taskService.getTasksByExecutor(executorId, page, size);
        return ResponseEntity.ok(tasks);
    }
}