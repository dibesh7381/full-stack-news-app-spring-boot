package NewsApp.com.example.NewsApp.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class ReporterLikeStatsDto {

    @Field("_id")
    private String reporterId;

    private Long totalLikes;

    private String reporterName;
}

