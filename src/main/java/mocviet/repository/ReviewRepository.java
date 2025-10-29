package mocviet.repository;

import mocviet.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    /**
     * Tìm review theo product_id
     */
    List<Review> findByProductIdAndIsHiddenFalse(Integer productId);
    
    /**
     * Tìm review theo user_id
     */
    List<Review> findByUserId(Integer userId);
    
    /**
     * Tìm review theo order_item_id (unique)
     */
    Optional<Review> findByOrderItemId(Integer orderItemId);
    
    /**
     * Kiểm tra user đã đánh giá order item này chưa
     */
    boolean existsByOrderItemId(Integer orderItemId);
    
    /**
     * Lấy danh sách OrderItem chưa đánh giá trong đơn hàng
     */
    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE oi.order.id = :orderId " +
           "AND oi.order.status = 'DELIVERED' " +
           "AND oi.order.user.id = :userId " +
           "AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.orderItem.id = oi.id)")
    List<mocviet.entity.OrderItem> findUnreviewedOrderItems(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    
    /**
     * Lấy danh sách đơn hàng có thể đánh giá (chỉ DELIVERED, có ít nhất 1 OrderItem chưa review)
     */
    @Query("SELECT DISTINCT o.id FROM Orders o " +
           "JOIN o.orderItems oi " +
           "WHERE o.user.id = :userId " +
           "AND o.status = 'DELIVERED' " +
           "AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.orderItem.id = oi.id)")
    List<Integer> findOrderIdsCanReview(@Param("userId") Integer userId);
    
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    // ===== QUERIES CHO UC-MGR-REV-ViewAllReviews =====
    
    /**
     * Tìm tất cả reviews với pagination (không hardcode ORDER BY)
     */
    Page<Review> findAll(Pageable pageable);
    
    /**
     * Đếm số đánh giá chưa trả lời
     */
    long countByManagerResponseIsNull();
    
    /**
     * Đếm số đánh giá đã ẩn
     */
    long countByIsHidden(Boolean isHidden);
    
    /**
     * Đếm số đánh giá theo rating
     */
    long countByRating(Integer rating);
    
    /**
     * Lọc đánh giá theo sản phẩm
     */
    Page<Review> findByProductId(Integer productId, Pageable pageable);
    
    /**
     * Lọc đánh giá theo rating
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);
    
    /**
     * Lọc đánh giá theo trạng thái ẩn/hiện
     */
    Page<Review> findByIsHidden(Boolean isHidden, Pageable pageable);
    
    /**
     * Lọc đánh giá chưa trả lời (không hardcode ORDER BY, để Pageable xử lý)
     */
    @Query("SELECT r FROM Review r WHERE r.managerResponse IS NULL")
    Page<Review> findUnansweredReviews(Pageable pageable);
    
    /**
     * Tìm kiếm đánh giá theo keyword (không hardcode ORDER BY)
     */
    @Query("SELECT r FROM Review r WHERE " +
           "(r.product.name LIKE %:keyword% OR r.user.fullName LIKE %:keyword%)")
    Page<Review> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Filter phức tạp: product + rating + hidden + unanswered (không hardcode ORDER BY)
     */
    @Query("SELECT r FROM Review r WHERE " +
           "(:productId IS NULL OR r.product.id = :productId) AND " +
           "(:rating IS NULL OR r.rating = :rating) AND " +
           "(:isHidden IS NULL OR r.isHidden = :isHidden) AND " +
           "(:unanswered = FALSE OR r.managerResponse IS NULL)")
    Page<Review> findByFilters(@Param("productId") Integer productId,
                                @Param("rating") Integer rating,
                                @Param("isHidden") Boolean isHidden,
                                @Param("unanswered") Boolean unanswered,
                                Pageable pageable);
    
    /**
     * Filter với keyword + filters (không hardcode ORDER BY)
     */
    @Query("SELECT r FROM Review r WHERE " +
           "(:keyword IS NULL OR r.product.name LIKE %:keyword% OR r.user.fullName LIKE %:keyword%) AND " +
           "(:productId IS NULL OR r.product.id = :productId) AND " +
           "(:rating IS NULL OR r.rating = :rating) AND " +
           "(:isHidden IS NULL OR r.isHidden = :isHidden) AND " +
           "(:unanswered = FALSE OR r.managerResponse IS NULL)")
    Page<Review> findByKeywordAndFilters(@Param("keyword") String keyword,
                                          @Param("productId") Integer productId,
                                          @Param("rating") Integer rating,
                                          @Param("isHidden") Boolean isHidden,
                                          @Param("unanswered") Boolean unanswered,
                                          Pageable pageable);
    
    // ===== QUERIES CHO UC-MGR-REV-ManageReviewAlerts =====
    
    /**
     * Tìm đánh giá mới chưa trả lời
     */
    @Query("SELECT r FROM Review r WHERE r.managerResponse IS NULL ORDER BY r.createdAt DESC")
    List<Review> findNewUnansweredReviews();
    
    /**
     * Tìm đánh giá có rating thấp (1-2 sao) chưa trả lời
     */
    @Query("SELECT r FROM Review r WHERE r.rating <= 2 AND r.managerResponse IS NULL ORDER BY r.createdAt DESC")
    List<Review> findLowRatingUnansweredReviews();
    
    /**
     * Tính average rating của toàn hệ thống (chỉ tính reviews không bị ẩn)
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.isHidden = FALSE")
    Double calculateAverageSystemRating();

    /**
     * Lấy các đánh giá 5 sao mới nhất, mỗi sản phẩm chỉ lấy 1 đánh giá.
     * Sử dụng native query vì ROW_NUMBER() không được hỗ trợ trực tiếp trong JPQL chuẩn.
     * Lưu ý: Product và User sẽ được load LAZY theo mặc định.
     */
    // @EntityGraph(attributePaths = {"product", "user"}) // <<< ĐÃ XÓA DÒNG NÀY
    @Query(value = "WITH RankedReviews AS (" +
            "SELECT r.*, ROW_NUMBER() OVER(PARTITION BY r.product_id ORDER BY r.created_at DESC) as rn " +
            "FROM Review r WHERE r.rating = 5 AND r.is_hidden = 0" + // Chỉ lấy 5 sao và không bị ẩn
            ") " +
            "SELECT rr.* FROM RankedReviews rr " +
            "WHERE rr.rn = 1 " + // Lấy review mới nhất cho mỗi sản phẩm
            "ORDER BY rr.created_at DESC", // Sắp xếp tổng thể theo thời gian mới nhất
            nativeQuery = true)
    List<Review> findTopBestReviewsDistinctProduct(Pageable pageable);
}
