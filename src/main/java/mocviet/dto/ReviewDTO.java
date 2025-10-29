package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Review hiển thị trên trang chủ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private Integer rating;
    private String content;
    private String imageUrl;
    private String customerName;
    private String productName;
    private String productSlug;
    private LocalDateTime createdAt;
    
    /**
     * Chuyển đổi từ Review entity sang ReviewDTO
     */
    public static ReviewDTO fromEntity(mocviet.entity.Review review) {
        if (review == null) {
            return null;
        }
        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .customerName(review.getUser() != null ? review.getUser().getFullName() : null)
                .productName(review.getProduct() != null ? review.getProduct().getName() : null)
                .productSlug(review.getProduct() != null ? review.getProduct().getSlug() : null)
                .createdAt(review.getCreatedAt())
                .build();
    }
}

