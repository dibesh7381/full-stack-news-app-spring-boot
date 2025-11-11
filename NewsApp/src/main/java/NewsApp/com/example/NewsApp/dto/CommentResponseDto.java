package NewsApp.com.example.NewsApp.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentResponseDto {
    private String id;
    private String newsId;
    private String userName;
    private String content;
    private Instant createdAt;
}

