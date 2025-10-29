package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho thống kê đánh giá trong Dashboard
 * Sử dụng cho UC-MGR-REV-ViewAllReviews
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDTO {
    private Long totalReviews;           // Tổng số đánh giá
    private Long unansweredReviews;      // Số đánh giá chưa trả lời
    private BigDecimal averageRating;    // Đánh giá trung bình hệ thống
    private Long newReviewsThisWeek;     // Đánh giá mới trong tuần
    private Long hiddenReviews;          // Số đánh giá đã ẩn
    private Long oneStarReviews;         // Số đánh giá 1 sao
    private Long twoStarReviews;         // Số đánh giá 2 sao
}

