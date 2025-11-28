package NewsApp.com.example.NewsApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private String id;
    private String newsId;
    private String userName;
    private String content;
    private Instant createdAt;
}

