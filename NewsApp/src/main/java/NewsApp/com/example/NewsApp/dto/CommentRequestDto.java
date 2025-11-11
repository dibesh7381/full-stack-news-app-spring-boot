package NewsApp.com.example.NewsApp.dto;

import lombok.Data;

@Data
public class CommentRequestDto {
    private String newsId;
    private String content;
}

