package NewsApp.com.example.NewsApp.service;

import NewsApp.com.example.NewsApp.dto.*;
import NewsApp.com.example.NewsApp.exception.CustomApiException;
import NewsApp.com.example.NewsApp.model.*;
import NewsApp.com.example.NewsApp.repository.*;
import NewsApp.com.example.NewsApp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LikeDislikeRepository likeDislikeRepository;
    private final NewsCommentRepository newsCommentRepository;


    // ====================== Auth Routes ======================

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        return new SignupResponse(user.getUsername(), user.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomApiException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(user.getEmail(), user.getRole(), token);
    }

    public ProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    public HomePageDTO getPublicHomepage() {
        return new HomePageDTO(
                "This Home Page is visible for all users",
                "This Page is Visible for all users"
        );
    }

    public ProfileResponse becomeReporter(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        user.setRole("REPORTER");
        userRepository.save(user);

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    // ====================== News Routes ======================

    public NewsResponseDto addNews(NewsRequestDto dto, String userEmail) {
        User reporter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!"REPORTER".equals(reporter.getRole())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "Only reporters can add news");
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setReporterId(reporter.getId());
        news.setCreatedAt(Instant.now());
        newsRepository.save(news);

        NewsResponseDto response = new NewsResponseDto();
        response.setId(news.getId());
        response.setTitle(news.getTitle());
        response.setContent(news.getContent());
        response.setReporterName(reporter.getUsername());
        response.setCreatedAt(news.getCreatedAt());
        return response;
    }

    public List<NewsResponseDto> getMyNews(String userEmail) {
        User reporter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        List<News> newsList = newsRepository.findByReporterId(reporter.getId());
        return newsList.stream().map(news -> {
            NewsResponseDto dto = new NewsResponseDto();
            dto.setId(news.getId());
            dto.setTitle(news.getTitle());
            dto.setContent(news.getContent());
            return dto;
        }).collect(Collectors.toList());
    }

    public NewsResponseDto updateNews(String newsId, NewsRequestDto dto, String userEmail) {
        User reporter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));

        if (!news.getReporterId().equals(reporter.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can only edit your own news");
        }

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        newsRepository.save(news);

        NewsResponseDto response = new NewsResponseDto();
        response.setId(news.getId());
        response.setTitle(news.getTitle());
        response.setContent(news.getContent());
        return response;
    }

    public void deleteNews(String newsId, String userEmail) {
        User reporter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));

        if (!news.getReporterId().equals(reporter.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can only delete your own news");
        }

        newsRepository.delete(news);
    }

    public List<NewsResponseDto> getAllNews(String userEmail) {
        // ✅ Get current logged-in user (for showing their like/dislike)
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        List<News> newsList = newsRepository.findAll();

        return newsList.stream().map(news -> {
            NewsResponseDto dto = new NewsResponseDto();
            dto.setId(news.getId());
            dto.setTitle(news.getTitle());
            dto.setContent(news.getContent());
            dto.setCreatedAt(news.getCreatedAt());

            // ✅ Reporter name
            userRepository.findById(news.getReporterId()).ifPresent(user -> {
                dto.setReporterName(user.getUsername());
            });

            // ✅ Like & Dislike counts
            long likeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "LIKE");
            long dislikeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "DISLIKE");
            dto.setLikeCount(likeCount);
            dto.setDislikeCount(dislikeCount);

            // ✅ Current user's action
            String userAction = likeDislikeRepository.findByUserIdAndNewsId(currentUser.getId(), news.getId())
                    .map(LikeDislike::getAction)
                    .orElse("NONE");
            dto.setUserAction(userAction);

            return dto;
        }).collect(Collectors.toList());
    }


    // ====================== Like / Dislike ======================

    public LikeDislikeResponse toggleLikeDislike(LikeDislikeRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));

        String action = request.getAction().toUpperCase();

        Optional<LikeDislike> existing = likeDislikeRepository.findByUserIdAndNewsId(user.getId(), news.getId());

        if (existing.isPresent()) {
            LikeDislike likeDislike = existing.get();
            if (likeDislike.getAction().equals(action)) {
                likeDislikeRepository.delete(likeDislike); // toggle off
            } else {
                likeDislike.setAction(action);
                likeDislikeRepository.save(likeDislike); // switch
            }
        } else {
            LikeDislike newAction = new LikeDislike();
            newAction.setUserId(user.getId());
            newAction.setNewsId(news.getId());
            newAction.setAction(action);
            likeDislikeRepository.save(newAction);
        }

        long likeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "LIKE");
        long dislikeCount = likeDislikeRepository.countByNewsIdAndAction(news.getId(), "DISLIKE");

        String userAction = likeDislikeRepository.findByUserIdAndNewsId(user.getId(), news.getId())
                .map(LikeDislike::getAction)
                .orElse("NONE");

        return new LikeDislikeResponse(news.getId(), likeCount, dislikeCount, userAction);
    }

    // ====================== Comments Feature ======================

    public CommentResponseDto addComment(CommentRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        News news = newsRepository.findById(dto.getNewsId())
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "News not found"));

        NewsComment comment = new NewsComment();
        comment.setNewsId(news.getId());
        comment.setUserId(user.getId());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(Instant.now());
        newsCommentRepository.save(comment);

        CommentResponseDto response = new CommentResponseDto();
        response.setId(comment.getId());
        response.setNewsId(comment.getNewsId());
        response.setUserName(user.getUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }

    public List<CommentResponseDto> getCommentsByNews(String newsId) {
        List<NewsComment> comments = newsCommentRepository.findByNewsId(newsId);

        return comments.stream().map(comment -> {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId(comment.getId());
            dto.setNewsId(comment.getNewsId());
            dto.setContent(comment.getContent());
            dto.setCreatedAt(comment.getCreatedAt());

            userRepository.findById(comment.getUserId()).ifPresent(u -> dto.setUserName(u.getUsername()));
            return dto;
        }).collect(Collectors.toList());
    }

    public void deleteComment(String commentId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        NewsComment comment = newsCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUserId().equals(user.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can delete only your own comment");
        }

        newsCommentRepository.delete(comment);
    }

    public CommentResponseDto updateComment(CommentUpdateRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "User not found"));

        NewsComment comment = newsCommentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUserId().equals(user.getId())) {
            throw new CustomApiException(HttpStatus.FORBIDDEN, "You can update only your own comment");
        }

        comment.setContent(dto.getContent());
        newsCommentRepository.save(comment);

        CommentResponseDto response = new CommentResponseDto();
        response.setId(comment.getId());
        response.setNewsId(comment.getNewsId());
        response.setUserName(user.getUsername());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }


}


