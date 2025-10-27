package mocviet.service.customer;

import mocviet.dto.ReviewRequestDTO;
import mocviet.dto.UnreviewedItemDTO;
import mocviet.entity.Review;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReviewService {
    
    /**
     * Lấy danh sách sản phẩm chưa đánh giá trong đơn hàng
     */
    List<UnreviewedItemDTO> getUnreviewedItems(Integer orderId);
    
    /**
     * Kiểm tra order item có thể đánh giá không
     */
    boolean canReviewOrderItem(Integer orderItemId);
    
    /**
     * Tạo đánh giá mới
     */
    Review createReview(ReviewRequestDTO request);
    
    /**
     * Upload ảnh đánh giá và cập nhật vào review
     * @param file Ảnh upload
     * @param reviewId ID của review (để tạo đường dẫn)
     * @return Đường dẫn ảnh đã lưu
     */
    String uploadReviewImage(MultipartFile file, Integer reviewId);
    
    /**
     * Cập nhật review với imageUrl
     */
    Review updateReviewImageUrl(Integer reviewId, String imageUrl);
    
    /**
     * Lấy đánh giá theo ID
     */
    Review getReviewById(Integer reviewId);
    
    /**
     * Lấy danh sách đánh giá của sản phẩm
     */
    List<Review> getProductReviews(Integer productId);
}

