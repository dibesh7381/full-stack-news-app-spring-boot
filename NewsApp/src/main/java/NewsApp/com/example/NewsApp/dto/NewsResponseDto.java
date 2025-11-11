package NewsApp.com.example.NewsApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseDto {
    private String id;
    private String title;
    private String content;
    private String reporterName;
    private Instant createdAt;

    // ✅ Add these new fields for like/dislike
    private long likeCount;
    private long dislikeCount;
    private String userAction; // "LIKE", "DISLIKE", or "NONE"
}


