package NewsApp.com.example.NewsApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeDislikeResponse {
    private String newsId;
    private long likeCount;
    private long dislikeCount;
    private String userAction; // user's current action (LIKE / DISLIKE / NONE)
}

