//package NewsApp.com.example.NewsApp.service;
//import NewsApp.com.example.NewsApp.dto.*;
//import NewsApp.com.example.NewsApp.exception.CustomApiException;
//import NewsApp.com.example.NewsApp.model.*;
//import NewsApp.com.example.NewsApp.repository.*;
//import NewsApp.com.example.NewsApp.security.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final NewsRepository newsRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//    private final LikeDislikeRepository likeDislikeRepository;
//    private final NewsCommentRepository newsCommentRepository;
//
//
//    // ====================== Auth Routes ======================
//
//    public SignupResponse signup(SignupRequest request) {
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Email already registered");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole("USER");
//        userRepository.save(user);
//
//        return new SignupResponse(user.getUsername(), user.getEmail());
//    }
//
//    public LoginResponse login(LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password");
//        }
//
//        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
//        return new LoginResponse(user.getEmail(), user.getRole(), token);
//    }
//
//    public ProfileResponse getProfileByEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
//    }
//
//    public HomePageDTO getPublicHomepage() {
//        return new HomePageDTO(
//                "This Home Page is visible for all users",
//                "This Page is Visible for all users"
//        );
//    }
//
//    public ProfileResponse becomeReporter(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        user.setRole("REPORTER");
//        userRepository.save(user);
//
//        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
//    }
//
//    // ====================== News Routes ======================
//
//    public NewsResponseDto addNews(NewsRequestDto dto, String userEmail) {
//        User reporter = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        if (!"REPORTER".equals(reporter.getRole())) {
//            throw new CustomApiException(HttpStatus.FORBIDDEN, "Only reporters can add news");
//        }
//
//        News news = new News();
//        news.setTitle(dto.getTitle());
//        news.setContent(dto.getContent());
//        news.setReporterId(reporter.getId());
//        news.setCreatedAt(Instant.now());
//        newsRepository.save(news);
//
//        NewsResponseDto response = new NewsResponseDto();
//        response.setId(news.getId());
//        response.setTitle(news.getTitle());
//        response.setContent(news.getContent());
//        response.setReporterName(reporter.getUsername());
//        response.setCreatedAt(news.getCreatedAt());
//        return response;
//    }
//
//    public List<NewsResponseDto> getMyNews(String userEmail) {
//        User reporter = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        List<News> newsList = newsRepository.findByReporterId(reporter.getId());
//        return newsList.stream().map(news -> {
//            NewsResponseDto dto = new NewsResponseDto();
//            dto.setId(news.getId());
//            dto.setTitle(news.getTitle());
//            dto.setContent(news.getContent());
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//    public NewsResponseDto updateNews(String newsId, NewsRequestDto dto, String userEmail) {
//        User reporter = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        News news = newsRepository.findById(newsId)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));
//
//        if (!news.getReporterId().equals(reporter.getId())) {
//            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can only edit your own news");
//        }
//
//        news.setTitle(dto.getTitle());
//        news.setContent(dto.getContent());
//        newsRepository.save(news);
//
//        NewsResponseDto response = new NewsResponseDto();
//        response.setId(news.getId());
//        response.setTitle(news.getTitle());
//        response.setContent(news.getContent());
//        return response;
//    }
//
//    public void deleteNews(String newsId, String userEmail) {
//        User reporter = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        News news = newsRepository.findById(newsId)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));
//
//        if (!news.getReporterId().equals(reporter.getId())) {
//            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can only delete your own news");
//        }
//
//        newsRepository.delete(news);
//    }
//
//    public List<NewsResponseDto> getAllNews(String userEmail) {
//        List<News> newsList = newsRepository.findAll();
//
//        return newsList.stream().map(news -> {
//            NewsResponseDto dto = new NewsResponseDto();
//            dto.setId(news.getId());
//            dto.setTitle(news.getTitle());
//            dto.setContent(news.getContent());
//            dto.setCreatedAt(news.getCreatedAt());
//
//            // ✅ Reporter name
//            userRepository.findById(news.getReporterId())
//                    .ifPresent(user -> dto.setReporterName(user.getUsername()));
//
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//
//
//    // ====================== Like / Dislike ======================
//
//    public LikeDislikeResponse toggleLikeDislike(LikeDislikeRequest request, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        News news = newsRepository.findById(request.getNewsId())
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));
//
//        String action = request.getAction().toUpperCase();
//
//        Optional<LikeDislike> existing = likeDislikeRepository.findByUserIdAndNewsId(user.getId(), news.getId());
//
//        if (existing.isPresent()) {
//            LikeDislike likeDislike = existing.get();
//            if (likeDislike.getAction().equals(action)) {
//                likeDislikeRepository.delete(likeDislike); // toggle off
//            } else {
//                likeDislike.setAction(action);
//                likeDislikeRepository.save(likeDislike); // switch
//            }
//        } else {
//            LikeDislike newAction = new LikeDislike();
//            newAction.setUserId(user.getId());
//            newAction.setNewsId(news.getId());
//            newAction.setAction(action);
//            likeDislikeRepository.save(newAction);
//        }
//
//        long likeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "LIKE");
//        long dislikeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "DISLIKE");
//
//        String userAction = likeDislikeRepository.findByUserIdAndNewsId(user.getId(), news.getId())
//                .map(LikeDislike::getAction)
//                .orElse("NONE");
//
//        return new LikeDislikeResponse(news.getId(), likeCount, dislikeCount, userAction);
//    }
//
//    public LikeDislikeResponse getReactionsForNews(String newsId, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        News news = newsRepository.findById(newsId)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));
//
//        long likeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "LIKE");
//        long dislikeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "DISLIKE");
//
//        String userAction = likeDislikeRepository.findByUserIdAndNewsId(user.getId(), news.getId())
//                .map(LikeDislike::getAction)
//                .orElse("NONE");
//
//        return new LikeDislikeResponse(newsId, likeCount, dislikeCount, userAction);
//    }
//
//
//
//    // ====================== Comments Feature ======================
//
//    public CommentResponseDto addComment(CommentRequestDto dto, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        News news = newsRepository.findById(dto.getNewsId())
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));
//
//        NewsComment comment = new NewsComment();
//        comment.setNewsId(news.getId());
//        comment.setUserId(user.getId());
//        comment.setContent(dto.getContent());
//        comment.setCreatedAt(Instant.now());
//        newsCommentRepository.save(comment);
//
//        CommentResponseDto response = new CommentResponseDto();
//        response.setId(comment.getId());
//        response.setNewsId(comment.getNewsId());
//        response.setUserName(user.getUsername());
//        response.setContent(comment.getContent());
//        response.setCreatedAt(comment.getCreatedAt());
//
//        return response;
//    }
//
//    public List<CommentResponseDto> getCommentsByNews(String newsId) {
//        List<NewsComment> comments = newsCommentRepository.findByNewsId(newsId);
//
//        return comments.stream().map(comment -> {
//            CommentResponseDto dto = new CommentResponseDto();
//            dto.setId(comment.getId());
//            dto.setNewsId(comment.getNewsId());
//            dto.setContent(comment.getContent());
//            dto.setCreatedAt(comment.getCreatedAt());
//
//            userRepository.findById(comment.getUserId()).ifPresent(u -> dto.setUserName(u.getUsername()));
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//    public void deleteComment(String commentId, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        NewsComment comment = newsCommentRepository.findById(commentId)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found"));
//
//        if (!comment.getUserId().equals(user.getId())) {
//            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can delete only your own comment");
//        }
//
//        newsCommentRepository.delete(comment);
//    }
//
//    public CommentResponseDto updateComment(CommentUpdateRequestDto dto, String userEmail) {
//        User user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));
//
//        NewsComment comment = newsCommentRepository.findById(dto.getCommentId())
//                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found"));
//
//        if (!comment.getUserId().equals(user.getId())) {
//            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can update only your own comment");
//        }
//
//        comment.setContent(dto.getContent());
//        newsCommentRepository.save(comment);
//
//        CommentResponseDto response = new CommentResponseDto();
//        response.setId(comment.getId());
//        response.setNewsId(comment.getNewsId());
//        response.setUserName(user.getUsername());
//        response.setContent(comment.getContent());
//        response.setCreatedAt(comment.getCreatedAt());
//
//        return response;
//    }
//}

package NewsApp.com.example.NewsApp.service;
import NewsApp.com.example.NewsApp.dto.*;
import NewsApp.com.example.NewsApp.exception.CustomApiException;
import NewsApp.com.example.NewsApp.model.*;
import NewsApp.com.example.NewsApp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    // ====================== AUTH ======================

    public SignupResponse signup(SignupRequest request) {

        Query q = new Query(Criteria.where("email").is(request.getEmail()));
        if (mongoTemplate.exists(q, User.class)) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        mongoTemplate.save(user);

        return new SignupResponse(user.getUsername(), user.getEmail());
    }


    public LoginResponse login(LoginRequest request) {

        Query q = new Query(Criteria.where("email").is(request.getEmail()));
        User user = mongoTemplate.findOne(q, User.class);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(user.getEmail(), user.getRole(), token);
    }


    public ProfileResponse getProfileByEmail(String email) {

        Query q = new Query(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(q, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }


    public ProfileResponse becomeReporter(String email) {

        Query q = new Query(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(q, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        user.setRole("REPORTER");
        mongoTemplate.save(user);

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }


    public HomePageDTO getPublicHomepage() {
        return new HomePageDTO(
                "This Home Page is visible for all users",
                "This Page is Visible for all users"
        );
    }


    // ====================== NEWS ======================


    public NewsResponseDto addNews(NewsRequestDto dto, String email) {

        User reporter = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        if (reporter == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        if (!"REPORTER".equals(reporter.getRole()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Only reporters can add news");

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setReporterId(reporter.getId());
        news.setCreatedAt(Instant.now());

        mongoTemplate.save(news);

        return new NewsResponseDto(news.getId(), news.getTitle(), news.getContent(),
                reporter.getUsername(), news.getCreatedAt());
    }


    public List<NewsResponseDto> getMyNews(String email) {

        User reporter = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        if (reporter == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query q = new Query(Criteria.where("reporterId").is(reporter.getId()));
        List<News> newsList = mongoTemplate.find(q, News.class);

        return newsList.stream().map(n ->
                new NewsResponseDto(n.getId(), n.getTitle(), n.getContent(),
                        reporter.getUsername(), n.getCreatedAt())
        ).collect(Collectors.toList());
    }


    public NewsResponseDto updateNews(String newsId, NewsRequestDto dto, String email) {

        User reporter = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        News news = mongoTemplate.findById(newsId, News.class);

        if (reporter == null || news == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");

        if (!news.getReporterId().equals(reporter.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your news");

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());

        mongoTemplate.save(news);

        return new NewsResponseDto(news.getId(), news.getTitle(), news.getContent(),
                reporter.getUsername(), news.getCreatedAt());
    }


    public void deleteNews(String newsId, String email) {

        User reporter = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        News news = mongoTemplate.findById(newsId, News.class);

        if (reporter == null || news == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");

        if (!news.getReporterId().equals(reporter.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your news");

        mongoTemplate.remove(news);
    }


    public List<NewsResponseDto> getAllNews(String email) {

        List<News> newsList = mongoTemplate.findAll(News.class);

        return newsList.stream().map(news -> {

            User reporter = mongoTemplate.findById(news.getReporterId(), User.class);

            return new NewsResponseDto(
                    news.getId(),
                    news.getTitle(),
                    news.getContent(),
                    reporter != null ? reporter.getUsername() : "Unknown",
                    news.getCreatedAt()
            );

        }).collect(Collectors.toList());
    }


    // ====================== LIKE / DISLIKE ======================


    public LikeDislikeResponse toggleLikeDislike(LikeDislikeRequest request, String email) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)), User.class);

        News news = mongoTemplate.findById(request.getNewsId(), News.class);

        if (user == null || news == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");

        String action = request.getAction().toUpperCase();

        Query q = new Query(
                Criteria.where("userId").is(user.getId())
                        .and("newsId").is(news.getId())
        );

        LikeDislike existing = mongoTemplate.findOne(q, LikeDislike.class);

        if (existing != null) {
            if (existing.getAction().equals(action)) {
                mongoTemplate.remove(existing);
            } else {
                existing.setAction(action);
                mongoTemplate.save(existing);
            }
        } else {
            LikeDislike newAction = new LikeDislike(null, user.getId(), news.getId(), action);
            mongoTemplate.save(newAction);
        }

        // COUNT LIKE/DISLIKE
        long likeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(news.getId()).and("action").is("LIKE")),
                LikeDislike.class);

        long dislikeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(news.getId()).and("action").is("DISLIKE")),
                LikeDislike.class);

        LikeDislike userAction = mongoTemplate.findOne(q, LikeDislike.class);

        return new LikeDislikeResponse(
                news.getId(),
                likeCount,
                dislikeCount,
                userAction != null ? userAction.getAction() : "NONE"
        );
    }



    public LikeDislikeResponse getReactionsForNews(String newsId, String email) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)), User.class);

        News news = mongoTemplate.findById(newsId, News.class);

        if (user == null || news == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");

        long likeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(newsId).and("action").is("LIKE")),
                LikeDislike.class);

        long dislikeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(newsId).and("action").is("DISLIKE")),
                LikeDislike.class);

        LikeDislike userAction = mongoTemplate.findOne(
                new Query(Criteria.where("userId").is(user.getId()).and("newsId").is(newsId)),
                LikeDislike.class
        );

        return new LikeDislikeResponse(
                newsId, likeCount, dislikeCount,
                userAction != null ? userAction.getAction() : "NONE"
        );
    }



    // ====================== COMMENTS ======================


    public CommentResponseDto addComment(CommentRequestDto dto, String email) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)), User.class);

        News news = mongoTemplate.findById(dto.getNewsId(), News.class);

        if (user == null || news == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");

        NewsComment comment = new NewsComment();
        comment.setNewsId(news.getId());
        comment.setUserId(user.getId());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(Instant.now());

        mongoTemplate.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getNewsId(),
                user.getUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }


    public List<CommentResponseDto> getCommentsByNews(String newsId) {

        Query q = new Query(Criteria.where("newsId").is(newsId));
        List<NewsComment> comments = mongoTemplate.find(q, NewsComment.class);

        return comments.stream().map(comment -> {

            User u = mongoTemplate.findById(comment.getUserId(), User.class);

            return new CommentResponseDto(
                    comment.getId(),
                    comment.getNewsId(),
                    u != null ? u.getUsername() : "Unknown",
                    comment.getContent(),
                    comment.getCreatedAt()
            );

        }).collect(Collectors.toList());
    }


    public void deleteComment(String commentId, String email) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)), User.class);

        NewsComment comment = mongoTemplate.findById(commentId, NewsComment.class);

        if (user == null || comment == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or Comment not found");

        if (!comment.getUserId().equals(user.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your comment");

        mongoTemplate.remove(comment);
    }


    public CommentResponseDto updateComment(CommentUpdateRequestDto dto, String email) {

        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)), User.class);

        NewsComment comment = mongoTemplate.findById(dto.getCommentId(), NewsComment.class);

        if (user == null || comment == null)
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or Comment not found");

        if (!comment.getUserId().equals(user.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your comment");

        comment.setContent(dto.getContent());
        mongoTemplate.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getNewsId(),
                user.getUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public List<ReporterLikeStatsDto> getTopLikedReporters() {

        Aggregation agg = Aggregation.newAggregation(

                Aggregation.addFields()
                        .addField("newsIdStr")
                        .withValue(ConvertOperators.ToString.toString("$_id"))
                        .build(),

                Aggregation.lookup("likes_dislikes", "newsIdStr", "newsId", "likes"),

                Aggregation.unwind("likes", true),

                Aggregation.match(Criteria.where("likes.action").is("LIKE")),

                Aggregation.group("reporterId")
                        .count().as("totalLikes"),

                Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalLikes")),

                Aggregation.limit(5)   // <-- ⭐ LIMIT ADDED HERE
        );

        AggregationResults<ReporterLikeStatsDto> result =
                mongoTemplate.aggregate(agg, "news", ReporterLikeStatsDto.class);

        result.getMappedResults().forEach(stat -> {
            User reporter = mongoTemplate.findById(stat.getReporterId(), User.class);
            if (reporter != null) stat.setReporterName(reporter.getUsername());
        });

        return result.getMappedResults();
    }

}

