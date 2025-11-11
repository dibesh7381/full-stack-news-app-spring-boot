package NewsApp.com.example.NewsApp.dto;

import lombok.Data;

@Data
public class LikeDislikeRequest {
    private String newsId;
    private String action; // "LIKE" or "DISLIKE"
}

