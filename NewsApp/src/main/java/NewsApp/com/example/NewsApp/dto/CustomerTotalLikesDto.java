package NewsApp.com.example.NewsApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerTotalLikesDto {
    private String userId;
    private String username;
    private long totalLikes;
    private List<UserLikedItemDto> likedPosts; // ⭐ list added
}
