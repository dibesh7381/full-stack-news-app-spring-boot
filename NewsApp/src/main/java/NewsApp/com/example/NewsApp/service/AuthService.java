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

    // Signup a new user
    public SignupResponse signup(SignupRequest request) {

        Criteria emailCriteria = Criteria.where("email").is(request.getEmail());
        Query emailQuery = new Query(emailCriteria);

        if (mongoTemplate.exists(emailQuery, User.class)) {
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

    // Login user and generate JWT
    public LoginResponse login(LoginRequest request) {

        Criteria emailCriteria = Criteria.where("email").is(request.getEmail());
        Query emailQuery = new Query(emailCriteria);

        User user = mongoTemplate.findOne(emailQuery, User.class);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(user.getEmail(), user.getRole(), token);
    }

    // Fetch profile of a user by email
    public ProfileResponse getProfileByEmail(String email) {

        Criteria emailCriteria = Criteria.where("email").is(email);
        Query emailQuery = new Query(emailCriteria);

        User user = mongoTemplate.findOne(emailQuery, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    // Convert user to Reporter
    public ProfileResponse becomeReporter(String email) {

        Criteria emailCriteria = Criteria.where("email").is(email);
        Query emailQuery = new Query(emailCriteria);

        User user = mongoTemplate.findOne(emailQuery, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        user.setRole("REPORTER");
        mongoTemplate.save(user);

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    // Public Home page
    public HomePageDTO getPublicHomepage() {
        return new HomePageDTO(
                "This Home Page is visible for all users",
                "This Page is Visible for all users"
        );
    }

    // ====================== NEWS ======================

    // Add news by reporter
    public NewsResponseDto addNews(NewsRequestDto dto, String email) {

        // Fetch user manually
        Criteria c1 = Criteria.where("email").is(email);
        Query q1 = new Query(c1);
        User reporter = mongoTemplate.findOne(q1, User.class);

        if (reporter == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!"REPORTER".equals(reporter.getRole())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Only reporters can add news");
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setReporterId(reporter.getId());
        news.setCreatedAt(Instant.now());

        mongoTemplate.save(news);

        return new NewsResponseDto(
                news.getId(), news.getTitle(), news.getContent(),
                reporter.getUsername(), news.getCreatedAt()
        );
    }


    // Get news of logged-in reporter
    public List<NewsResponseDto> getMyNews(String email) {

        // Fetch user manually
        Criteria cu = Criteria.where("email").is(email);
        Query userQuery = new Query(cu);
        User reporter = mongoTemplate.findOne(userQuery, User.class);

        if (reporter == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        Criteria c1 = Criteria.where("reporterId").is(reporter.getId());
        Query q1 = new Query(c1);

        List<News> newsList = mongoTemplate.find(q1, News.class);

        return newsList.stream()
                .map(n -> new NewsResponseDto(
                        n.getId(), n.getTitle(), n.getContent(),
                        reporter.getUsername(), n.getCreatedAt()))
                .collect(Collectors.toList());
    }


    // Update news
    public NewsResponseDto updateNews(String newsId, NewsRequestDto dto, String email) {

        // Fetch user manually
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User reporter = mongoTemplate.findOne(qu, User.class);

        if (reporter == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        News news = mongoTemplate.findById(newsId, News.class);

        if (news == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");
        }

        if (!news.getReporterId().equals(reporter.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your news");
        }

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());

        mongoTemplate.save(news);

        return new NewsResponseDto(
                news.getId(), news.getTitle(), news.getContent(),
                reporter.getUsername(), news.getCreatedAt()
        );
    }


    // Delete news
    public void deleteNews(String newsId, String email) {

        // Fetch user manually
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User reporter = mongoTemplate.findOne(qu, User.class);

        if (reporter == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        News news = mongoTemplate.findById(newsId, News.class);

        if (news == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");
        }

        if (!news.getReporterId().equals(reporter.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your news");
        }

        mongoTemplate.remove(news);
    }


    // Get all news
    public List<NewsResponseDto> getAllNews(String email) {

        List<News> newsList = mongoTemplate.findAll(News.class);

        return newsList.stream()
                .map(news -> {
                    User reporter = mongoTemplate.findById(news.getReporterId(), User.class);
                    String name = reporter != null ? reporter.getUsername() : "Unknown";

                    return new NewsResponseDto(
                            news.getId(), news.getTitle(), news.getContent(),
                            name, news.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }



    // ====================== LIKE / DISLIKE ======================

    public LikeDislikeResponse toggleLikeDislike(LikeDislikeRequest request, String email) {

        // Fetch user manually
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User user = mongoTemplate.findOne(qu, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        News news = mongoTemplate.findById(request.getNewsId(), News.class);
        if (news == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");
        }

        String action = request.getAction().toUpperCase();

        // userId AND newsId
        Criteria c1 = Criteria.where("userId").is(user.getId());
        Criteria c2 = Criteria.where("newsId").is(news.getId());
        Criteria finalCrit = new Criteria().andOperator(c1, c2);

        Query q1 = new Query(finalCrit);
        LikeDislike existing = mongoTemplate.findOne(q1, LikeDislike.class);

        if (existing != null) {
            if (existing.getAction().equals(action)) {
                mongoTemplate.remove(existing);
            } else {
                existing.setAction(action);
                mongoTemplate.save(existing);
            }
        } else {
            LikeDislike newEntry =
                    new LikeDislike(null, user.getId(), news.getId(), action);
            mongoTemplate.save(newEntry);
        }

        // Like count
        Criteria lc1 = Criteria.where("newsId").is(news.getId());
        Criteria lc2 = Criteria.where("action").is("LIKE");
        Criteria likeFinal = new Criteria().andOperator(lc1, lc2);
        Query likeQuery = new Query(likeFinal);
        long likeCount = mongoTemplate.count(likeQuery, LikeDislike.class);

        // Dislike count
        Criteria dc1 = Criteria.where("newsId").is(news.getId());
        Criteria dc2 = Criteria.where("action").is("DISLIKE");
        Criteria disFinal = new Criteria().andOperator(dc1, dc2);
        Query dislikeQuery = new Query(disFinal);
        long dislikeCount = mongoTemplate.count(dislikeQuery, LikeDislike.class);

        LikeDislike userAction = mongoTemplate.findOne(q1, LikeDislike.class);

        return new LikeDislikeResponse(
                news.getId(),
                likeCount,
                dislikeCount,
                userAction != null ? userAction.getAction() : "NONE"
        );
    }

    public LikeDislikeResponse getReactionsForNews(String newsId, String email) {

        // Fetch user
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User user = mongoTemplate.findOne(qu, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        News news = mongoTemplate.findById(newsId, News.class);
        if (news == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");
        }

        // Like count
        Criteria lc1 = Criteria.where("newsId").is(newsId);
        Criteria lc2 = Criteria.where("action").is("LIKE");
        Criteria lcFinal = new Criteria().andOperator(lc1, lc2);
        long likeCount = mongoTemplate.count(new Query(lcFinal), LikeDislike.class);

        // Dislike count
        Criteria dc1 = Criteria.where("newsId").is(newsId);
        Criteria dc2 = Criteria.where("action").is("DISLIKE");
        Criteria dcFinal = new Criteria().andOperator(dc1, dc2);
        long dislikeCount = mongoTemplate.count(new Query(dcFinal), LikeDislike.class);

        // User reaction
        Criteria uc1 = Criteria.where("userId").is(user.getId());
        Criteria uc2 = Criteria.where("newsId").is(newsId);
        Criteria userFinal = new Criteria().andOperator(uc1, uc2);
        LikeDislike userAction = mongoTemplate.findOne(new Query(userFinal), LikeDislike.class);

        return new LikeDislikeResponse(
                newsId,
                likeCount,
                dislikeCount,
                userAction != null ? userAction.getAction() : "NONE"
        );
    }

    // ====================== COMMENTS ======================

    // Add comment
    public CommentResponseDto addComment(CommentRequestDto dto, String email) {

        // Fetch user
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User user = mongoTemplate.findOne(qu, User.class);

        // Fetch news
        News news = mongoTemplate.findById(dto.getNewsId(), News.class);

        if (user == null || news == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User or News not found");
        }

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


    // Get all comments for a news
    public List<CommentResponseDto> getCommentsByNews(String newsId) {

        Criteria c1 = Criteria.where("newsId").is(newsId);
        Query q1 = new Query(c1);

        List<NewsComment> comments = mongoTemplate.find(q1, NewsComment.class);

        return comments.stream()
                .map(comment -> {

                    User u = mongoTemplate.findById(comment.getUserId(), User.class);
                    String username = (u != null) ? u.getUsername() : "Unknown";

                    return new CommentResponseDto(
                            comment.getId(),
                            comment.getNewsId(),
                            username,
                            comment.getContent(),
                            comment.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }


    // Delete comment
    public void deleteComment(String commentId, String email) {

        // Fetch user
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User user = mongoTemplate.findOne(qu, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        NewsComment comment = mongoTemplate.findById(commentId, NewsComment.class);

        if (comment == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        if (!comment.getUserId().equals(user.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your comment");
        }

        mongoTemplate.remove(comment);
    }


    // Update comment
    public CommentResponseDto updateComment(CommentUpdateRequestDto dto, String email) {

        // Fetch user
        Criteria cu = Criteria.where("email").is(email);
        Query qu = new Query(cu);
        User user = mongoTemplate.findOne(qu, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        NewsComment comment = mongoTemplate.findById(dto.getCommentId(), NewsComment.class);

        if (comment == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        if (!comment.getUserId().equals(user.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Not your comment");
        }

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

        // Stage 1 → Convert _id to string
        AddFieldsOperation convertIdToString = Aggregation.addFields()
                .addField("newsIdStr")
                .withValue(ConvertOperators.ToString.toString("$_id"))
                .build();

        // Stage 2 → Lookup likes_dislikes table
        LookupOperation lookupLikes = LookupOperation.newLookup()
                .from("likes_dislikes")
                .localField("newsIdStr")
                .foreignField("newsId")
                .as("likes");

        // Stage 3 → Unwind likes array
        UnwindOperation unwindLikes = Aggregation.unwind("likes", true);

        // Stage 4 → Filter only LIKE actions
        Criteria likeCriteria = Criteria.where("likes.action").is("LIKE");
        MatchOperation matchLikes = Aggregation.match(likeCriteria);

        // Stage 5 → Group by reporterId and count likes
        GroupOperation groupByReporter = Aggregation.group("reporterId")
                .count().as("totalLikes");

        // Stage 6 → Sort by total likes DESC
        SortOperation sortByLikes = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "totalLikes")
        );

        // Stage 7 → Limit 5 reporters
        LimitOperation limitTop5 = Aggregation.limit(5);

        // Build pipeline
        Aggregation agg = Aggregation.newAggregation(
                convertIdToString,
                lookupLikes,
                unwindLikes,
                matchLikes,
                groupByReporter,
                sortByLikes,
                limitTop5
        );

        AggregationResults<ReporterLikeStatsDto> result =
                mongoTemplate.aggregate(agg, "news", ReporterLikeStatsDto.class);

        // Add reporter names manually
        result.getMappedResults().forEach(stat -> {
            User reporter = mongoTemplate.findById(stat.getReporterId(), User.class);
            if (reporter != null) {
                stat.setReporterName(reporter.getUsername());
            }
        });

        return result.getMappedResults();
    }


    public List<NewsLikeStatsDto> getTop5NewsByReporter(String email) {

        // Step 1 — Fetch reporter
        Criteria userCriteria = Criteria.where("email").is(email);
        Query userQuery = new Query(userCriteria);
        User reporter = mongoTemplate.findOne(userQuery, User.class);

        if (reporter == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        String reporterId = reporter.getId();


        // Step 2 — STAGE: Match only reporter's news
        Criteria newsCriteria = Criteria.where("reporterId").is(reporterId);
        MatchOperation matchReporterNews = Aggregation.match(newsCriteria);


        // Step 3 — STAGE: Convert _id → String field
        AddFieldsOperation convertIdToString = Aggregation.addFields()
                .addField("newsIdStr")
                .withValue(ConvertOperators.ToString.toString("$_id"))
                .build();


        // Step 4 — STAGE: Lookup likes_dislikes
        LookupOperation lookupLikes = LookupOperation.newLookup()
                .from("likes_dislikes")
                .localField("newsIdStr")
                .foreignField("newsId")
                .as("likes");


        // Step 5 — STAGE: Unwind likes array
        UnwindOperation unwindLikes = Aggregation.unwind("likes", true);


        // Step 6 — STAGE: Match only LIKE actions
        Criteria likeCriteria = Criteria.where("likes.action").is("LIKE");
        MatchOperation matchLikes = Aggregation.match(likeCriteria);


        // Step 7 — STAGE: Group by newsId and count likes
        GroupOperation groupByNews = Aggregation.group("newsIdStr")
                .first("title").as("title")
                .count().as("totalLikes");


        // Step 8 — STAGE: Sort by totalLikes DESC
        SortOperation sortByLikes = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "totalLikes")
        );


        // Step 9 — STAGE: Limit to Top 5
        LimitOperation limitTop5 = Aggregation.limit(5);


        // Build pipeline
        Aggregation agg = Aggregation.newAggregation(
                matchReporterNews,
                convertIdToString,
                lookupLikes,
                unwindLikes,
                matchLikes,
                groupByNews,
                sortByLikes,
                limitTop5
        );


        // Execute
        AggregationResults<NewsLikeStatsDto> result =
                mongoTemplate.aggregate(agg, "news", NewsLikeStatsDto.class);

        return result.getMappedResults();
    }


    // ====================== CUSTOMER LIKE ANALYTICS ======================

    public CustomerTotalLikesDto getCustomerTotalLikes(String email) {

        // STEP 1 — Fetch user
        Criteria cEmail = Criteria.where("email").is(email);
        Query qEmail = new Query(cEmail);
        User user = mongoTemplate.findOne(qEmail, User.class);

        if (user == null) {
            throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        }


        // STEP 2 — Aggregation: Count total LIKE actions

        // Build match criteria separately
        Criteria cUserId = Criteria.where("userId").is(user.getId());
        Criteria cLike = Criteria.where("action").is("LIKE");

        Criteria finalMatch = new Criteria().andOperator(cUserId, cLike);

        MatchOperation matchUserLikes = Aggregation.match(finalMatch);

        GroupOperation groupLikes = Aggregation.group("userId")
                .count().as("totalLikes");

        Aggregation agg = Aggregation.newAggregation(
                matchUserLikes,
                groupLikes
        );

        AggregationResults<CustomerTotalLikesDto> result =
                mongoTemplate.aggregate(agg, "likes_dislikes", CustomerTotalLikesDto.class);

        CustomerTotalLikesDto dto = result.getUniqueMappedResult();

        // No likes
        if (dto == null) {
            dto = new CustomerTotalLikesDto();
            dto.setTotalLikes(0);
        }

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());


        // STEP 3 — Fetch all LIKE records separately

        Criteria cLikeUser = Criteria.where("userId").is(user.getId());
        Criteria cLikeAction = Criteria.where("action").is("LIKE");

        Criteria likeQueryCriteria = new Criteria().andOperator(cLikeUser, cLikeAction);

        Query likeQuery = new Query(likeQueryCriteria);

        List<LikeDislike> likedRecords = mongoTemplate.find(likeQuery, LikeDislike.class);


        // STEP 4 — Convert to small DTO items

        List<UserLikedItemDto> likedPosts = likedRecords.stream()
                .map(record -> {
                    News news = mongoTemplate.findById(record.getNewsId(), News.class);

                    UserLikedItemDto item = new UserLikedItemDto();
                    item.setNewsId(news.getId());
                    item.setTitle(news.getTitle());
                    return item;
                })
                .collect(Collectors.toList());

        dto.setLikedPosts(likedPosts);

        return dto;
    }

}

