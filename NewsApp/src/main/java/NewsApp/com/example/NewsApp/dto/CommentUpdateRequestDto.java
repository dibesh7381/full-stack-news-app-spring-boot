package NewsApp.com.example.NewsApp.dto;

import lombok.Data;

@Data
public class CommentUpdateRequestDto {
    private String commentId;
    private String content;
}

