package NewsApp.com.example.NewsApp.controller;

import NewsApp.com.example.NewsApp.dto.*;
import NewsApp.com.example.NewsApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    // ====================== Auth Routes ======================
    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponseDto<SignupResponse>> signup(@RequestBody SignupRequest request) {
        SignupResponse data = authService.signup(request);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "User registered successfully", data));
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponseDto<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse data = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("jwt", data.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponseDto<>(true, "Login successful", data));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<?>> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponseDto<>(true, "Logged out successfully", null));
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<ProfileResponse>> getProfile() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfileResponse profile = authService.getProfileByEmail(email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Profile fetched successfully", profile));
    }

    @GetMapping("/home")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponseDto<?>> getHomePage() {
        HomePageDTO dto = authService.getPublicHomepage();
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Home Page fetched successfully", dto));
    }

    @PostMapping("/become-reporter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<ProfileResponse>> becomeReporter() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfileResponse updatedProfile = authService.becomeReporter(email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Role updated to REPORTER", updatedProfile));
    }

    // ====================== News Routes ======================
    @PostMapping("/news/add")
    @PreAuthorize("hasAuthority('REPORTER')")
    public ResponseEntity<ApiResponseDto<NewsResponseDto>> addNews(@RequestBody NewsRequestDto dto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NewsResponseDto news = authService.addNews(dto, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "News added successfully", news));
    }

    @GetMapping("/news/my-news")
    @PreAuthorize("hasAuthority('REPORTER')")
    public ResponseEntity<ApiResponseDto<List<NewsResponseDto>>> getMyNews() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NewsResponseDto> newsList = authService.getMyNews(email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Fetched reporter's news", newsList));
    }

    @PutMapping("/news/update/{id}")
    @PreAuthorize("hasAuthority('REPORTER')")
    public ResponseEntity<ApiResponseDto<NewsResponseDto>> updateNews(@PathVariable String id, @RequestBody NewsRequestDto dto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        NewsResponseDto updated = authService.updateNews(id, dto, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "News updated successfully", updated));
    }

    @DeleteMapping("/news/delete/{id}")
    @PreAuthorize("hasAuthority('REPORTER')")
    public ResponseEntity<ApiResponseDto<?>> deleteNews(@PathVariable String id) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.deleteNews(id, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "News deleted successfully", null));
    }

    @GetMapping("/news/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<NewsResponseDto>>> getAllNews() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NewsResponseDto> allNews = authService.getAllNews(email);  // ← Email pass kar
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Fetched all news successfully", allNews));
    }


    // ====================== Like / Dislike Route ======================
    @PostMapping("/news/like-dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<LikeDislikeResponse>> toggleLikeDislike(@RequestBody LikeDislikeRequest request) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LikeDislikeResponse response = authService.toggleLikeDislike(request, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Action updated successfully", response));
    }

    @GetMapping("/news/{newsId}/reactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<LikeDislikeResponse>> getReactions(@PathVariable String newsId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LikeDislikeResponse response = authService.getReactionsForNews(newsId, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Fetched like/dislike info", response));
    }



    // ====================== Comment Routes ======================

    @PostMapping("/news/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> addComment(@RequestBody CommentRequestDto dto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponseDto response = authService.addComment(dto, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Comment added successfully", response));
    }

    @GetMapping("/news/{newsId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getComments(@PathVariable String newsId) {
        List<CommentResponseDto> comments = authService.getCommentsByNews(newsId);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Fetched all comments", comments));
    }

    @DeleteMapping("/news/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<?>> deleteComment(@PathVariable String id) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.deleteComment(id, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Comment deleted successfully", null));
    }

    @PutMapping("/news/comment/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(@RequestBody CommentUpdateRequestDto dto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponseDto updated = authService.updateComment(dto, email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Comment updated successfully", updated));
    }

    // ====================== Reporter Like Stats ======================

    @GetMapping("/news/top-liked-reporters")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<ReporterLikeStatsDto>>> getTopLikedReporters() {
        List<ReporterLikeStatsDto> stats = authService.getTopLikedReporters();
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Top liked reporters fetched", stats));
    }

    // ====================== Reporter: Top 5 Liked News ======================

    @GetMapping("/news/my-top-liked")
    @PreAuthorize("hasAuthority('REPORTER')")
    public ResponseEntity<ApiResponseDto<List<NewsLikeStatsDto>>> getMyTopLikedNews() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NewsLikeStatsDto> stats = authService.getTop5NewsByReporter(email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Top 5 liked news fetched", stats));
    }

    // ====================== Customer Total Likes ======================

    @GetMapping("/customer/total-likes")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponseDto<CustomerTotalLikesDto>> getCustomerTotalLikes() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomerTotalLikesDto dto = authService.getCustomerTotalLikes(email);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "Total likes given by user fetched successfully", dto));
    }

}

