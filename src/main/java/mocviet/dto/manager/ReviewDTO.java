package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Review trong Manager
 * Sử dụng cho UC-MGR-REV-ViewAllReviews
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private String customerName;
    private String productName;
    private String productSlug;
    private Integer rating;
    private String content;
    private String imageUrl;
    private Boolean isHidden;
    private String managerResponse;
    private String managerName;
    private LocalDateTime responseAt;
    private LocalDateTime createdAt;
    private Integer productId;
    private Integer userId;
}

