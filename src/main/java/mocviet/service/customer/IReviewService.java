package mocviet.service.customer;

import mocviet.dto.customer.ReviewDTO;
import mocviet.dto.customer.ReviewRequestDTO;
import mocviet.dto.customer.UnreviewedItemDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReviewService {
    
    List<UnreviewedItemDTO> getUnreviewedItems(Integer orderId);
    
    boolean canReviewOrderItem(Integer orderItemId);
    
    ReviewDTO createReview(ReviewRequestDTO request);
    
    String uploadReviewImage(MultipartFile file, Integer reviewId);
    
    ReviewDTO updateReviewImageUrl(Integer reviewId, String imageUrl);
    
    ReviewDTO getReviewById(Integer reviewId);
    
    List<ReviewDTO> getProductReviews(Integer productId);
}

