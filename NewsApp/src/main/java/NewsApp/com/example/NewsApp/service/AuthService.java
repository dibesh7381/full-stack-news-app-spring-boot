//package NewsApp.com.example.NewsApp.service;
//
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
//
//

package NewsApp.com.example.NewsApp.service;

import NewsApp.com.example.NewsApp.dto.*;
import NewsApp.com.example.NewsApp.exception.CustomApiException;
import NewsApp.com.example.NewsApp.model.*;
import NewsApp.com.example.NewsApp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    // ====================== Auth Routes ======================

    public SignupResponse signup(SignupRequest request) {

        Query emailQuery = new Query(Criteria.where("email").is(request.getEmail()));
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

        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    public HomePageDTO getPublicHomepage() {
        return new HomePageDTO(
                "This Home Page is visible for all users",
                "This Page is Visible for all users"
        );
    }

    public ProfileResponse becomeReporter(String email) {

        Query q = new Query(Criteria.where("email").is(email));
        Update update = new Update().set("role", "REPORTER");

        FindAndModifyOptions opt = FindAndModifyOptions.options().returnNew(true);

        User updated = mongoTemplate.findAndModify(q, update, opt, User.class);

        if (updated == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        return new ProfileResponse(updated.getUsername(), updated.getEmail(), updated.getRole());
    }



    // ====================== News Routes ======================

    public NewsResponseDto addNews(NewsRequestDto dto, String userEmail) {

        Query q = new Query(Criteria.where("email").is(userEmail));
        User reporter = mongoTemplate.findOne(q, User.class);

        if (reporter == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");
        if (!"REPORTER".equals(reporter.getRole()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Only reporters can add news");

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setReporterId(reporter.getId());
        news.setCreatedAt(Instant.now());

        mongoTemplate.save(news);

        NewsResponseDto res = new NewsResponseDto();
        res.setId(news.getId());
        res.setTitle(news.getTitle());
        res.setContent(news.getContent());
        res.setReporterName(reporter.getUsername());
        res.setCreatedAt(news.getCreatedAt());
        return res;
    }

    public List<NewsResponseDto> getMyNews(String userEmail) {

        Query q = new Query(Criteria.where("email").is(userEmail));
        User reporter = mongoTemplate.findOne(q, User.class);

        if (reporter == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query newsQuery = new Query(Criteria.where("reporterId").is(reporter.getId()));
        List<News> newsList = mongoTemplate.find(newsQuery, News.class);

        return newsList.stream().map(n -> {
            NewsResponseDto dto = new NewsResponseDto();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setContent(n.getContent());
            return dto;
        }).collect(Collectors.toList());
    }

    public NewsResponseDto updateNews(String newsId, NewsRequestDto dto, String userEmail) {

        // find reporter
        Query userQ = new Query(Criteria.where("email").is(userEmail));
        User reporter = mongoTemplate.findOne(userQ, User.class);
        if (reporter == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        // find news
        Query q = new Query(Criteria.where("_id").is(newsId));
        News found = mongoTemplate.findOne(q, News.class);
        if (found == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");

        if (!found.getReporterId().equals(reporter.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can only edit your own news");

        // UPDATE + RETURN NEW DOC
        Update update = new Update()
                .set("title", dto.getTitle())
                .set("content", dto.getContent());

        FindAndModifyOptions opt = FindAndModifyOptions.options().returnNew(true);
        News updated = mongoTemplate.findAndModify(q, update, opt, News.class);

        NewsResponseDto response = new NewsResponseDto();
        response.setId(updated.getId());
        response.setTitle(updated.getTitle());
        response.setContent(updated.getContent());
        response.setReporterName(reporter.getUsername());
        response.setCreatedAt(updated.getCreatedAt());

        return response;
    }

    public void deleteNews(String newsId, String userEmail) {

        Query userQ = new Query(Criteria.where("email").is(userEmail));
        User reporter = mongoTemplate.findOne(userQ, User.class);

        if (reporter == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query q = new Query(Criteria.where("_id").is(newsId));
        News news = mongoTemplate.findOne(q, News.class);

        if (news == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");

        if (!news.getReporterId().equals(reporter.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can delete only your own news");

        mongoTemplate.remove(q, News.class);
    }

    public List<NewsResponseDto> getAllNews(String userEmail) {

        List<News> list = mongoTemplate.findAll(News.class);

        return list.stream().map(n -> {
            NewsResponseDto dto = new NewsResponseDto();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setContent(n.getContent());
            dto.setCreatedAt(n.getCreatedAt());

            // find reporter
            Query q = new Query(Criteria.where("_id").is(n.getReporterId()));
            User rep = mongoTemplate.findOne(q, User.class);
            if (rep != null) dto.setReporterName(rep.getUsername());

            return dto;
        }).collect(Collectors.toList());
    }



    // ====================== Like / Dislike ======================

    public LikeDislikeResponse toggleLikeDislike(LikeDislikeRequest req, String userEmail) {

        // user
        Query uQ = new Query(Criteria.where("email").is(userEmail));
        User user = mongoTemplate.findOne(uQ, User.class);
        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        // news exists?
        Query nQ = new Query(Criteria.where("_id").is(req.getNewsId()));
        News news = mongoTemplate.findOne(nQ, News.class);
        if (news == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");

        String action = req.getAction().toUpperCase();

        // existing?
        Query likeQ = new Query(
                Criteria.where("userId").is(user.getId())
                        .and("newsId").is(news.getId())
        );

        LikeDislike existing = mongoTemplate.findOne(likeQ, LikeDislike.class);

        if (existing != null) {
            if (existing.getAction().equals(action)) {
                mongoTemplate.remove(likeQ, LikeDislike.class);     // toggle off
            } else {
                Update upd = new Update().set("action", action);
                mongoTemplate.findAndModify(likeQ, upd, FindAndModifyOptions.options().returnNew(true), LikeDislike.class);
            }
        } else {
            LikeDislike ld = new LikeDislike();
            ld.setUserId(user.getId());
            ld.setNewsId(news.getId());
            ld.setAction(action);
            mongoTemplate.save(ld);
        }

        long likeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(news.getId()).and("action").is("LIKE")),
                LikeDislike.class
        );

        long dislikeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(news.getId()).and("action").is("DISLIKE")),
                LikeDislike.class
        );

        LikeDislike finalAction = mongoTemplate.findOne(likeQ, LikeDislike.class);

        return new LikeDislikeResponse(
                news.getId(),
                likeCount,
                dislikeCount,
                finalAction != null ? finalAction.getAction() : "NONE"
        );
    }

    public LikeDislikeResponse getReactionsForNews(String newsId, String userEmail) {

        Query uq = new Query(Criteria.where("email").is(userEmail));
        User user = mongoTemplate.findOne(uq, User.class);
        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query nq = new Query(Criteria.where("_id").is(newsId));
        News news = mongoTemplate.findOne(nq, News.class);
        if (news == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");

        long likeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(newsId).and("action").is("LIKE")),
                LikeDislike.class
        );

        long dislikeCount = mongoTemplate.count(
                new Query(Criteria.where("newsId").is(newsId).and("action").is("DISLIKE")),
                LikeDislike.class
        );

        Query myActionQ = new Query(
                Criteria.where("userId").is(user.getId()).and("newsId").is(newsId)
        );
        LikeDislike myAction = mongoTemplate.findOne(myActionQ, LikeDislike.class);

        return new LikeDislikeResponse(newsId, likeCount, dislikeCount,
                myAction != null ? myAction.getAction() : "NONE");
    }



    // ====================== Comments Feature ======================

    public CommentResponseDto addComment(CommentRequestDto dto, String userEmail) {

        Query uq = new Query(Criteria.where("email").is(userEmail));
        User user = mongoTemplate.findOne(uq, User.class);
        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query nq = new Query(Criteria.where("_id").is(dto.getNewsId()));
        News news = mongoTemplate.findOne(nq, News.class);
        if (news == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "News not found");

        NewsComment comment = new NewsComment();
        comment.setNewsId(news.getId());
        comment.setUserId(user.getId());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(Instant.now());

        mongoTemplate.save(comment);

        CommentResponseDto res = new CommentResponseDto();
        res.setId(comment.getId());
        res.setNewsId(comment.getNewsId());
        res.setUserName(user.getUsername());
        res.setContent(comment.getContent());
        res.setCreatedAt(comment.getCreatedAt());
        return res;
    }

    public List<CommentResponseDto> getCommentsByNews(String newsId) {

        Query cq = new Query(Criteria.where("newsId").is(newsId));
        List<NewsComment> comments = mongoTemplate.find(cq, NewsComment.class);

        return comments.stream().map(c -> {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId(c.getId());
            dto.setNewsId(c.getNewsId());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt());

            Query uq = new Query(Criteria.where("_id").is(c.getUserId()));
            User u = mongoTemplate.findOne(uq, User.class);
            if (u != null) dto.setUserName(u.getUsername());

            return dto;
        }).collect(Collectors.toList());
    }

    public void deleteComment(String commentId, String userEmail) {

        Query uq = new Query(Criteria.where("email").is(userEmail));
        User user = mongoTemplate.findOne(uq, User.class);
        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query cq = new Query(Criteria.where("_id").is(commentId));
        NewsComment comment = mongoTemplate.findOne(cq, NewsComment.class);

        if (comment == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found");
        if (!comment.getUserId().equals(user.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can delete only your own comment");

        mongoTemplate.remove(cq, NewsComment.class);
    }

    public CommentResponseDto updateComment(CommentUpdateRequestDto dto, String userEmail) {

        Query uq = new Query(Criteria.where("email").is(userEmail));
        User user = mongoTemplate.findOne(uq, User.class);
        if (user == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "User not found");

        Query cq = new Query(Criteria.where("_id").is(dto.getCommentId()));
        NewsComment comment = mongoTemplate.findOne(cq, NewsComment.class);

        if (comment == null) throw new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found");
        if (!comment.getUserId().equals(user.getId()))
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can update only your own comment");

        Update update = new Update().set("content", dto.getContent());

        FindAndModifyOptions opt = FindAndModifyOptions.options().returnNew(true);

        NewsComment updated = mongoTemplate.findAndModify(cq, update, opt, NewsComment.class);

        CommentResponseDto res = new CommentResponseDto();
        res.setId(updated.getId());
        res.setNewsId(updated.getNewsId());
        res.setUserName(user.getUsername());
        res.setContent(updated.getContent());
        res.setCreatedAt(updated.getCreatedAt());
        return res;
    }
}

