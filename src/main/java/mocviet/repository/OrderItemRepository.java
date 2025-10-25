package mocviet.repository;

import mocviet.entity.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    /**
     * Tìm OrderItem theo order ID
     */
    List<OrderItem> findByOrderIdOrderByIdAsc(Integer orderId);
    
    /**
     * Tìm OrderItem theo order ID với fetch ProductVariant, Color và Product
     */
    @EntityGraph(attributePaths = {"variant", "variant.color", "variant.product"})
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId ORDER BY oi.id ASC")
    List<OrderItem> findByOrderIdWithVariantAndColor(@Param("orderId") Integer orderId);
    
    /**
     * Tìm OrderItem theo order ID và variant ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.variant.id = :variantId")
    Optional<OrderItem> findByOrderIdAndVariantId(@Param("orderId") Integer orderId, @Param("variantId") Integer variantId);
    
    /**
     * Kiểm tra OrderItem đã có review chưa
     */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.id = :orderItemId AND oi.review IS NOT NULL")
    boolean hasReview(@Param("orderItemId") Integer orderItemId);
    
    /**
     * Tìm OrderItem chưa có review trong đơn hàng
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.review IS NULL")
    List<OrderItem> findUnreviewedItemsByOrderId(@Param("orderId") Integer orderId);
}
