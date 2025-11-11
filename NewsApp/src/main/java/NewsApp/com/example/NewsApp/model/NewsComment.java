package NewsApp.com.example.NewsApp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class NewsComment {
    @Id
    private String id;

    private String newsId;    // 👈 Linked to News
    private String userId;    // 👈 Linked to User
    private String content;

    private Instant createdAt;
}

