package ru.FirstSecurityApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.FirstSecurityApp.dtos.CommentDTO;
import ru.FirstSecurityApp.models.Comment;
import ru.FirstSecurityApp.services.CommentService;

import java.util.List;

/**
 * @swagger
 * @class CommentController
 * @description Контроллер для управления комментариями к задачам.
 *              Обрабатывает запросы на добавление, удаление и получение комментариев.
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * @swagger
     * @method POST
     * @path /comments/tasks/{taskId}
     * @description Добавление нового комментария к задаче.
     * @param taskId Идентификатор задачи, к которой добавляется комментарий.
     * @param comment Объект комментария для добавления.
     * @return DTO объекта добавленного комментария.
     */
    @PostMapping("/tasks/{taskId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long taskId, @RequestBody Comment comment) {
        Comment saveComment = commentService.addComment(taskId, comment);
        return ResponseEntity.ok(commentService.commentConvertToCommentDto(saveComment));
    }

    /**
     * @swagger
     * @method DELETE
     * @path /comments/{id}
     * @description Удаление комментария по его идентификатору.
     * @param id Идентификатор удаляемого комментария.
     * @return Ответ без содержимого при успешном удалении.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @swagger
     * @method GET
     * @path /comments/tasks/{taskId}
     * @description Получение списка всех комментариев к задаче.
     * @param taskId Идентификатор задачи, для которой получаем комментарии.
     * @return Список комментариев, связанных с задачей.
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }

    /**
     * @swagger
     * @method GET
     * @path /comments/task/{taskId}
     * @description Получение пагинированного списка комментариев к задаче.
     * @param taskId Идентификатор задачи, для которой получаем комментарии.
     * @param page Номер страницы для пагинации (по умолчанию 0).
     * @param size Размер страницы для пагинации (по умолчанию 10).
     * @return Пагинированный список DTO объектов комментариев.
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CommentDTO> comments = commentService.getCommentsByTask(taskId, page, size);
        return ResponseEntity.ok(comments);
    }
}
