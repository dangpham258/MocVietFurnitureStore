package mocviet.repository;

import mocviet.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

