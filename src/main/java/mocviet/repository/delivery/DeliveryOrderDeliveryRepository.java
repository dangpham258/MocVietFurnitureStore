package mocviet.repository.delivery;

import mocviet.entity.OrderDelivery;
import mocviet.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // Thêm import

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryOrderDeliveryRepository extends JpaRepository<OrderDelivery, Integer> {

    @EntityGraph(attributePaths = {"order", "order.address", "order.user"})
    List<OrderDelivery> findByDeliveryTeamIdAndStatusInOrderByUpdatedAtDesc(
            Integer deliveryTeamId,
            List<OrderDelivery.DeliveryStatus> statuses
    );

    @EntityGraph(attributePaths = {
            "order", "order.address", "order.user",
            "order.orderItems", "order.orderItems.variant", "order.orderItems.variant.product", "order.orderItems.variant.color",
            "deliveryHistories"
    })
    Optional<OrderDelivery> findByIdAndDeliveryTeamId(Integer id, Integer deliveryTeamId);

    Optional<OrderDelivery> findByOrderIdAndDeliveryTeamId(Integer orderId, Integer deliveryTeamId);

    /**
     * Lấy lịch sử giao hàng DONE của một đội (phân trang).
     * Sửa tên: Bỏ OrderByUpdatedAtDesc
     */
     @EntityGraph(attributePaths = {"order", "order.address"})
     Page<OrderDelivery> findByDeliveryTeamIdAndStatus( // <<<--- SỬA TÊN Ở ĐÂY
             Integer deliveryTeamId,
             OrderDelivery.DeliveryStatus status,
             Pageable pageable
     );
     /**
      * Đếm số đơn hàng DONE (giao/thu hồi) theo team ID và khoảng thời gian (từ startTime đến hiện tại)
      */
     @Query("SELECT COUNT(od) FROM OrderDelivery od " +
            "WHERE od.deliveryTeam.id = :teamId " +
            "AND od.status = :status " +
            "AND od.updatedAt >= :startTime")
     long countByDeliveryTeamIdAndStatusAndUpdatedAtAfter(
             @Param("teamId") Integer teamId,
             @Param("status") OrderDelivery.DeliveryStatus status,
             @Param("startTime") LocalDateTime startTime
     );
     
}