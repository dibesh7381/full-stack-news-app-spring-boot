package NewsApp.com.example.NewsApp.repository;

import NewsApp.com.example.NewsApp.model.NewsComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsCommentRepository extends MongoRepository<NewsComment, String> {
    List<NewsComment> findByNewsId(String newsId);
    List<NewsComment> findByUserId(String userId);
}
