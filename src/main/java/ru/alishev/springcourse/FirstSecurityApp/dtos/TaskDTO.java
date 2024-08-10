package ru.alishev.springcourse.FirstSecurityApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    @NotEmpty(message = "Заголовок не может быть пустым")
    @Size(max = 255, message = "Заголовок не может быть длиннее 255 символов")
    private String title;

    @NotEmpty(message = "Описание не может быть пустым")
    private String description;

    @NotEmpty(message = "Статус не может быть пустым")
    private String status;

    @NotEmpty(message = "Приоритет не может быть пустым")
    private String priority;

    @NotNull(message = "ID автора не может быть пустым")
    private Long authorId;

    private Long executorId;

    // Опционально: Можно добавить список комментариев, если требуется
    // private List<CommentDTO> comments;
}
