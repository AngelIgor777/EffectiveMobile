package ru.FirstSecurityApp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.FirstSecurityApp.models.Person;
import ru.FirstSecurityApp.models.Task;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthor(Person author, Pageable pageable);
    Page<Task> findByExecutor(Person executor, Pageable pageable);

}
