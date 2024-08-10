package ru.FirstSecurityApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private Long taskId;
    private Long authorId;
    private String content;
    private String authorName;
}
