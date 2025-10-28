package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Review;
import mocviet.entity.User;
import mocviet.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service quản lý đánh giá/bình luận cho Manager
 * Implements các use case:
 * - UC-MGR-REV-ViewAllReviews: Xem tất cả đánh giá
 * - UC-MGR-REV-RespondToReview: Trả lời đánh giá của khách hàng
 * - UC-MGR-REV-HideShowReview: Ẩn/hiện đánh giá
 * - UC-MGR-REV-ManageReviewAlerts: Quản lý cảnh báo đánh giá mới
 */
@Service
@RequiredArgsConstructor
public class ReviewManagementService {
    
    private final ReviewRepository reviewRepository;
    
    // ===== UC-MGR-REV-ViewAllReviews: XEM TẤT CẢ ĐÁNH GIÁ =====
    
    /**
     * Lấy thống kê dashboard cho đánh giá
     */
    @Transactional(readOnly = true)
    public ReviewStatsDTO getReviewStats() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        
        Long totalReviews = reviewRepository.count();
        Long unansweredReviews = reviewRepository.countByManagerResponseIsNull();
        Double avgRatingDouble = reviewRepository.calculateAverageSystemRating();
        BigDecimal averageRating = avgRatingDouble != null ? 
            BigDecimal.valueOf(avgRatingDouble).setScale(1, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        Long newReviewsThisWeek = reviewRepository.countByCreatedAtAfter(oneWeekAgo);
        Long hiddenReviews = reviewRepository.countByIsHidden(true);
        Long oneStarReviews = reviewRepository.countByRating(1);
        Long twoStarReviews = reviewRepository.countByRating(2);
        
        return new ReviewStatsDTO(
            totalReviews,
            unansweredReviews,
            averageRating,
            newReviewsThisWeek,
            hiddenReviews,
            oneStarReviews,
            twoStarReviews
        );
    }
    
    /**
     * Lấy danh sách đánh giá với filter và pagination
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviews(ReviewFilterDTO filter, Pageable pageable) {
        Page<Review> reviews;
        
        // Xử lý filter phức tạp
        String keyword = filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty() ? 
                        filter.getKeyword().trim() : null;
        Integer productId = filter.getProductId();
        Integer rating = filter.getRating();
        Boolean isHidden = filter.getIsHidden();
        Boolean unanswered = filter.getUnanswered() != null ? filter.getUnanswered() : false;
        
        // Gọi query phù hợp (Pageable đã chứa Sort từ Controller)
        if (keyword != null) {
            reviews = reviewRepository.findByKeywordAndFilters(
                keyword, productId, rating, isHidden, unanswered, pageable);
        } else if (productId != null || rating != null || isHidden != null || unanswered) {
            reviews = reviewRepository.findByFilters(
                productId, rating, isHidden, unanswered, pageable);
        } else {
            reviews = reviewRepository.findAll(pageable);
        }
        
        // Convert sang DTO
        List<ReviewDTO> dtos = reviews.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, reviews.getTotalElements());
    }
    
    /**
     * Lấy chi tiết một đánh giá
     */
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        return convertToDTO(review);
    }
    
    // ===== UC-MGR-REV-RespondToReview: TRẢ LỜI ĐÁNH GIÁ =====
    
    /**
     * Trả lời đánh giá của khách hàng
     * Trigger TR_Review_NotifyManagerResponse sẽ tự động tạo thông báo cho customer
     */
    @Transactional
    public void respondToReview(Integer reviewId, String response, User manager) {
        // Validate
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập nội dung phản hồi");
        }
        if (response.length() > 1000) {
            throw new IllegalArgumentException("Nội dung phản hồi quá dài (tối đa 1000 ký tự)");
        }
        
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        
        // Kiểm tra đã có phản hồi chưa
        if (review.getManagerResponse() != null) {
            throw new IllegalArgumentException("Đánh giá đã có phản hồi");
        }
        
        // Cập nhật phản hồi
        review.setManagerResponse(response.trim());
        review.setManager(manager);
        review.setResponseAt(LocalDateTime.now());
        
        reviewRepository.save(review);
        // Trigger TR_Review_NotifyManagerResponse sẽ tự động chạy
    }
    
    /**
     * Sửa phản hồi đã gửi
     */
    @Transactional
    public void updateResponse(Integer reviewId, String response, User manager) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập nội dung phản hồi");
        }
        if (response.length() > 1000) {
            throw new IllegalArgumentException("Nội dung phản hồi quá dài (tối đa 1000 ký tự)");
        }
        
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        
        // Cập nhật phản hồi
        review.setManagerResponse(response.trim());
        review.setManager(manager);
        review.setResponseAt(LocalDateTime.now());
        
        reviewRepository.save(review);
    }
    
    /**
     * Xóa phản hồi
     */
    @Transactional
    public void deleteResponse(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        
        review.setManagerResponse(null);
        review.setManager(null);
        review.setResponseAt(null);
        
        reviewRepository.save(review);
    }
    
    // ===== UC-MGR-REV-HideShowReview: ẨN/HIỆN ĐÁNH GIÁ =====
    
    /**
     * Ẩn/hiện đánh giá
     * Trigger TR_Review_UpdateProductRating sẽ tự động cập nhật rating sản phẩm
     */
    @Transactional
    public void toggleReviewVisibility(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        
        // Toggle trạng thái
        review.setIsHidden(!review.getIsHidden());
        
        reviewRepository.save(review);
        // Trigger TR_Review_UpdateProductRating sẽ tự động chạy và cập nhật rating
    }
    
    /**
     * Ẩn/hiện hàng loạt
     */
    @Transactional
    public void bulkToggleVisibility(List<Integer> reviewIds, Boolean hide) {
        for (Integer id : reviewIds) {
            Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đánh giá #" + id + " không tồn tại"));
            review.setIsHidden(hide);
            reviewRepository.save(review);
        }
        // Triggers sẽ tự động cập nhật rating cho các sản phẩm liên quan
    }
    
    // ===== UC-MGR-REV-ManageReviewAlerts: QUẢN LÝ CẢNH BÁO ĐÁNH GIÁ MỚI =====
    
    /**
     * Lấy danh sách cảnh báo đánh giá
     */
    @Transactional(readOnly = true)
    public List<ReviewAlertDTO> getReviewAlerts() {
        List<Review> unansweredReviews = reviewRepository.findNewUnansweredReviews();
        
        // Tạo alerts từ tất cả reviews chưa trả lời
        List<ReviewAlertDTO> alerts = unansweredReviews.stream()
            .map(r -> {
                String alertType = "NEW_REVIEW";
                String priority = "MEDIUM";
                
                // Ưu tiên cao cho rating thấp
                if (r.getRating() <= 2) {
                    alertType = "LOW_RATING";
                    priority = "HIGH";
                }
                
                // Kiểm tra nội dung tiêu cực (giản lược)
                if (r.getContent() != null && 
                    (r.getContent().contains("tệ") || r.getContent().contains("kém") || 
                     r.getContent().contains("không tốt") || r.getContent().contains("thất vọng"))) {
                    alertType = "NEGATIVE_CONTENT";
                    priority = "HIGH";
                }
                
                return new ReviewAlertDTO(
                    r.getId(),
                    alertType,
                    r.getUser().getFullName(),
                    r.getProduct().getName(),
                    r.getProduct().getSlug(),
                    r.getRating(),
                    r.getContent(),
                    r.getCreatedAt(),
                    priority
                );
            })
            .collect(Collectors.toList());
        
        // Sắp xếp theo priority: HIGH trước
        alerts.sort((a, b) -> {
            if (a.getPriority().equals("HIGH") && !b.getPriority().equals("HIGH")) return -1;
            if (!a.getPriority().equals("HIGH") && b.getPriority().equals("HIGH")) return 1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        
        return alerts;
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Convert Review entity sang ReviewDTO
     */
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCustomerName(review.getUser().getFullName());
        dto.setProductName(review.getProduct().getName());
        dto.setProductSlug(review.getProduct().getSlug());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setImageUrl(review.getImageUrl());
        dto.setIsHidden(review.getIsHidden());
        dto.setManagerResponse(review.getManagerResponse());
        dto.setManagerName(review.getManager() != null ? review.getManager().getFullName() : null);
        dto.setResponseAt(review.getResponseAt());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        return dto;
    }
}

