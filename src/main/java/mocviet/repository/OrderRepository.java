package mocviet.repository;

import mocviet.entity.Orders;
import mocviet.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    
    /**
     * Tìm đơn hàng theo user ID (không fetch collection để tránh cảnh báo HHH90003004)
     */
    Page<Orders> findByUserId(Integer userId, Pageable pageable);
    
    /**
     * Fetch orderItems cho một danh sách orders (bao gồm variant và color)
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant", "orderItems.variant.color"})
    @Query("SELECT o FROM Orders o WHERE o.id IN :orderIds")
    List<Orders> findByIdsWithOrderItems(@Param("orderIds") List<Integer> orderIds);
    
    /**
     * Tìm đơn hàng theo user ID và status
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant", "orderItems.variant.product", "orderItems.variant.color"})
    List<Orders> findByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, Orders.OrderStatus status);
    
    /**
     * Tìm đơn hàng theo user ID và return status
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant", "orderItems.variant.product", "orderItems.variant.color"})
    List<Orders> findByUserIdAndReturnStatusOrderByCreatedAtDesc(Integer userId, Orders.ReturnStatus returnStatus);
    
    /**
     * Tìm đơn hàng theo ID và user ID (đảm bảo user chỉ xem được đơn của mình)
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant", "orderItems.variant.product", "orderItems.variant.color", "address", "coupon"})
    @Query("SELECT o FROM Orders o WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Orders> findByIdAndUserId(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    
    /**
     * Kiểm tra đơn hàng có thể hủy không (chỉ khi PENDING)
     */
    @Query("SELECT COUNT(o) > 0 FROM Orders o WHERE o.id = :orderId AND o.user.id = :userId AND o.status = 'PENDING'")
    boolean canCancelOrder(@Param("orderId") Integer orderId, @Param("userId") Integer userId);
    
    /**
     * Kiểm tra đơn hàng có thể yêu cầu trả không (DELIVERED và trong 30 ngày)
     */
    @Query("SELECT COUNT(o) > 0 FROM Orders o WHERE o.id = :orderId AND o.user.id = :userId AND o.status = 'DELIVERED' " +
           "AND o.createdAt >= :thirtyDaysAgo AND (o.returnStatus IS NULL OR o.returnStatus NOT IN ('REJECTED', 'PROCESSED'))")
    boolean canRequestReturn(@Param("orderId") Integer orderId, @Param("userId") Integer userId, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    /**
     * Đếm số đơn hàng theo status của user
     */
    long countByUserIdAndStatus(Integer userId, Orders.OrderStatus status);
    
    /**
     * Đếm số đơn hàng theo return status của user
     */
    long countByUserIdAndReturnStatus(Integer userId, Orders.ReturnStatus returnStatus);
    
    /**
     * Tìm đơn hàng có thể đánh giá (DELIVERED và chưa đánh giá)
     */
    @Query("SELECT o FROM Orders o WHERE o.user.id = :userId AND o.status = 'DELIVERED' " +
           "AND EXISTS (SELECT oi FROM OrderItem oi WHERE oi.order.id = o.id AND oi.review IS NULL)")
    List<Orders> findOrdersCanReview(@Param("userId") Integer userId);
    
    /**
     * Tìm đơn hàng có thể mua lại (CANCELLED hoặc RETURNED)
     */
    List<Orders> findByUserIdAndStatusInOrderByCreatedAtDesc(Integer userId, List<Orders.OrderStatus> statuses);
    
    /**
     * Kiểm tra user đã từng dùng coupon này chưa
     */
    @Query("SELECT COUNT(o) > 0 FROM Orders o WHERE o.user.id = :userId AND o.coupon.code = :couponCode")
    boolean hasUserUsedCoupon(@Param("userId") Integer userId, @Param("couponCode") String couponCode);
}
