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
    @Operation(summary = "Create a new task", description = "Create a new task with the given details")
    @ApiResponse(responseCode = "200", description = "Task successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input or author not found")
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        Optional<Person> author = peopleRepository.findById(task.getAuthor().getId());
        Optional<Person> executor = task.getExecutor() != null ? peopleRepository.findById(task.getExecutor().getId()) : Optional.empty();

        if (author.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Task savedTask = taskService.createTask(task);
        return ResponseEntity.ok(taskService.taskConvertToTaskDto(savedTask));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a task", description = "Add a comment to the specified task")
    @ApiResponse(responseCode = "200", description = "Comment added successfully")
    public ResponseEntity<Void> addComment(@PathVariable Long id, @RequestParam String commentText, @RequestParam String currentUsername) {
        taskService.addComment(id, commentText, currentUsername);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/priority")
    @Operation(summary = "Update task priority", description = "Update the priority of a specific task")
    @ApiResponse(responseCode = "200", description = "Task priority updated successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskDTO> updateTaskPriority(@PathVariable Long id, @RequestParam String priority) {
        try {
            TaskDTO updatedTask = taskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Update the details of an existing task")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Delete the task with the specified ID")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    @ApiResponse(responseCode = "200", description = "Task retrieved successfully")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        Task task = taskService.getTask(id);
        return ResponseEntity.ok(taskService.taskConvertToTaskDto(task));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task (only for the executor)")
    @ApiResponse(responseCode = "200", description = "Task status updated successfully")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        TaskDTO updatedTask = taskService.updateTaskStatus(id, status, currentUsername);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/executor/{executorId}")
    @Operation(summary = "Get tasks by executor", description = "Retrieve a paginated list of tasks assigned to a specific executor")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<Page<TaskDTO>> getTasksByExecutor(
            @PathVariable Long executorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TaskDTO> tasks = taskService.getTasksByExecutor(executorId, page, size);
        return ResponseEntity.ok(tasks);
    }
}
