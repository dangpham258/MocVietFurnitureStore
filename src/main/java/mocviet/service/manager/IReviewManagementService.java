package mocviet.service.manager;

import mocviet.dto.manager.*;
import mocviet.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewManagementService {
    ReviewStatsDTO getReviewStats();
    Page<ReviewDTO> getReviews(ReviewFilterDTO filter, Pageable pageable);
    ReviewDTO getReviewById(Integer id);
    void respondToReview(Integer reviewId, String response, User manager);
    void updateResponse(Integer reviewId, String response, User manager);
    void deleteResponse(Integer reviewId);
    void toggleReviewVisibility(Integer reviewId);
    void bulkToggleVisibility(List<Integer> reviewIds, Boolean hide);
    List<ReviewAlertDTO> getReviewAlerts();
}


