package ru.FirstSecurityApp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.models.Task;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId);
    Page<Comment> findByTask(Task task, Pageable pageable);
}
