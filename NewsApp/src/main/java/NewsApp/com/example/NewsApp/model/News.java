package NewsApp.com.example.NewsApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    private String id;

    private String title;
    private String content;

    private Instant createdAt = Instant.now();

    private String reporterId; // MongoDB main simple reference
}

