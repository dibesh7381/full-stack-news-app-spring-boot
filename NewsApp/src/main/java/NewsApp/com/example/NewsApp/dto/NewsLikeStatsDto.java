package NewsApp.com.example.NewsApp.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class NewsLikeStatsDto {
    @Field("_id")
    private String newsId;
    private String title;
    private Long totalLikes;
}

