package ru.alishev.springcourse.FirstSecurityApp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alishev.springcourse.FirstSecurityApp.dtos.TaskDTO;
import ru.alishev.springcourse.FirstSecurityApp.models.Task;
import ru.alishev.springcourse.FirstSecurityApp.repositories.TaskRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task task) {
        if (taskRepository.existsById(id)) {
            task.setId(id);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    @Transactional
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    public TaskDTO taskDtoResponse(Task task) {
        return TaskDTO.builder().
                id(task.getId())
                .title(task.getStatus())
                .description(task.getDescription()).
                status(task.getStatus()).
                priority(task.getPriority())
                .authorId(task.getAuthor().getId())
                .executorId(task.getExecutor().getId())
                .build();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
