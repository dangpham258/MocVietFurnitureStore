package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho cảnh báo đánh giá mới
 * Sử dụng cho UC-MGR-REV-ManageReviewAlerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAlertDTO {
    private Integer id;
    private String alertType;        // NEW_REVIEW, LOW_RATING, NEGATIVE_CONTENT
    private String customerName;
    private String productName;
    private String productSlug;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private String priority;         // HIGH, MEDIUM, LOW
}

