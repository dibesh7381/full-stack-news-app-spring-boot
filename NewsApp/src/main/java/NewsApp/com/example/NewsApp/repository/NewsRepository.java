package NewsApp.com.example.NewsApp.repository;

import NewsApp.com.example.NewsApp.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findByReporterId(String reporterId);
}

