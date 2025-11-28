package NewsApp.com.example.NewsApp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes_dislikes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDislike {

    @Id
    private String id;
    private String userId;
    private String newsId;
    private String action; // "LIKE" or "DISLIKE"
}
