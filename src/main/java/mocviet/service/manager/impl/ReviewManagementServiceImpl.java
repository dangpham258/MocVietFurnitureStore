package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Review;
import mocviet.entity.User;
import mocviet.repository.ReviewRepository;
import mocviet.service.manager.IReviewManagementService;
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

@Service
@RequiredArgsConstructor
public class ReviewManagementServiceImpl implements IReviewManagementService {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public ReviewStatsDTO getReviewStats() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        Long totalReviews = reviewRepository.count();
        Long unansweredReviews = reviewRepository.countByManagerResponseIsNull();
        Double avgRatingDouble = reviewRepository.calculateAverageSystemRating();
        BigDecimal averageRating = avgRatingDouble != null ? BigDecimal.valueOf(avgRatingDouble).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Long newReviewsThisWeek = reviewRepository.countByCreatedAtAfter(oneWeekAgo);
        Long hiddenReviews = reviewRepository.countByIsHidden(true);
        Long oneStarReviews = reviewRepository.countByRating(1);
        Long twoStarReviews = reviewRepository.countByRating(2);
        return new ReviewStatsDTO(totalReviews, unansweredReviews, averageRating, newReviewsThisWeek, hiddenReviews, oneStarReviews, twoStarReviews);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviews(ReviewFilterDTO filter, Pageable pageable) {
        Page<Review> reviews;
        String keyword = filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty() ? filter.getKeyword().trim() : null;
        Integer productId = filter.getProductId();
        Integer rating = filter.getRating();
        Boolean isHidden = filter.getIsHidden();
        Boolean unanswered = filter.getUnanswered() != null ? filter.getUnanswered() : false;
        if (keyword != null) {
            reviews = reviewRepository.findByKeywordAndFilters(keyword, productId, rating, isHidden, unanswered, pageable);
        } else if (productId != null || rating != null || isHidden != null || unanswered) {
            reviews = reviewRepository.findByFilters(productId, rating, isHidden, unanswered, pageable);
        } else {
            reviews = reviewRepository.findAll(pageable);
        }
        List<ReviewDTO> dtos = reviews.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, reviews.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        return convertToDTO(review);
    }

    @Override
    @Transactional
    public void respondToReview(Integer reviewId, String response, User manager) {
        if (response == null || response.trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập nội dung phản hồi");
        if (response.length() > 1000) throw new IllegalArgumentException("Nội dung phản hồi quá dài (tối đa 1000 ký tự)");
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        if (review.getManagerResponse() != null) throw new IllegalArgumentException("Đánh giá đã có phản hồi");
        review.setManagerResponse(response.trim());
        review.setManager(manager);
        review.setResponseAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void updateResponse(Integer reviewId, String response, User manager) {
        if (response == null || response.trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập nội dung phản hồi");
        if (response.length() > 1000) throw new IllegalArgumentException("Nội dung phản hồi quá dài (tối đa 1000 ký tự)");
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        review.setManagerResponse(response.trim());
        review.setManager(manager);
        review.setResponseAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteResponse(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        review.setManagerResponse(null);
        review.setManager(null);
        review.setResponseAt(null);
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void toggleReviewVisibility(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
        review.setIsHidden(!review.getIsHidden());
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void bulkToggleVisibility(List<Integer> reviewIds, Boolean hide) {
        for (Integer id : reviewIds) {
            Review review = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Đánh giá #" + id + " không tồn tại"));
            review.setIsHidden(hide);
            reviewRepository.save(review);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewAlertDTO> getReviewAlerts() {
        List<Review> unansweredReviews = reviewRepository.findNewUnansweredReviews();
        List<ReviewAlertDTO> alerts = unansweredReviews.stream().map(r -> {
            String alertType = "NEW_REVIEW";
            String priority = "MEDIUM";
            if (r.getRating() <= 2) { alertType = "LOW_RATING"; priority = "HIGH"; }
            if (r.getContent() != null && (r.getContent().contains("tệ") || r.getContent().contains("kém") || r.getContent().contains("không tốt") || r.getContent().contains("thất vọng"))) {
                alertType = "NEGATIVE_CONTENT";
                priority = "HIGH";
            }
            return new ReviewAlertDTO(r.getId(), alertType, r.getUser().getFullName(), r.getProduct().getName(), r.getProduct().getSlug(), r.getRating(), r.getContent(), r.getCreatedAt(), priority);
        }).collect(Collectors.toList());
        alerts.sort((a, b) -> {
            if (a.getPriority().equals("HIGH") && !b.getPriority().equals("HIGH")) return -1;
            if (!a.getPriority().equals("HIGH") && b.getPriority().equals("HIGH")) return 1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        return alerts;
    }

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


