package NewsApp.com.example.NewsApp.repository;

import NewsApp.com.example.NewsApp.model.LikeDislike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikeDislikeRepository extends MongoRepository<LikeDislike, String> {
    Optional<LikeDislike> findByUserIdAndNewsId(String userId, String newsId);
    long countByNewsIdAndAction(String newsId, String action);
    List<LikeDislike> findByNewsId(String newsId);
}

