package mocviet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocviet.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    long countByStatus(Orders.OrderStatus status);

    /**
     * Tìm đơn hàng theo khoảng thời gian
     */
    List<Orders> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Tính tổng doanh thu từ đơn hàng đã giao
     */
    @Query("SELECT COALESCE(SUM(oi.qty * oi.unitPrice), 0) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status = :status " +
           "AND o.createdAt BETWEEN :start AND :end")
    Long calculateRevenue(@Param("status") Orders.OrderStatus status,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    /**
     * Đếm tổng số đơn hàng
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createdAt BETWEEN :start AND :end")
    long countOrdersInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Tính trung bình giá trị đơn hàng
     */
    @Query("SELECT COALESCE(AVG(oi.qty * oi.unitPrice), 0) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status = :status " +
           "AND o.createdAt BETWEEN :start AND :end")
    Double calculateAverageOrderValue(@Param("status") Orders.OrderStatus status,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    /**
     * Lấy doanh thu theo ngày (cho biểu đồ) - cú pháp SQL Server
     */
    @Query("SELECT CAST(o.createdAt AS DATE) as date, " +
           "COALESCE(SUM(oi.qty * oi.unitPrice), 0) as revenue " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status = :status " +
           "AND o.createdAt BETWEEN :start AND :end " +
           "GROUP BY CAST(o.createdAt AS DATE) " +
           "ORDER BY CAST(o.createdAt AS DATE)")
    List<Object[]> getRevenueByDate(@Param("status") Orders.OrderStatus status,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    /**
     * Đếm số lượng khách hàng đặt hàng
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) " +
           "FROM Orders o " +
           "WHERE o.createdAt BETWEEN :start AND :end")
    long countDistinctCustomers(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    /**
     * Đếm đơn hàng theo trạng thái trong khoảng thời gian
     */
    @Query("SELECT COUNT(o) FROM Orders o " +
           "WHERE o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByStatusInPeriod(@Param("status") Orders.OrderStatus status,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    /**
     * Lấy sản phẩm top theo doanh thu
     */
    @Query("SELECT p.id, p.name, " +
           "SUM(oi.qty) as totalSold, " +
           "SUM(oi.qty * oi.unitPrice) as revenue " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.variant v " +
           "JOIN v.product p " +
           "WHERE o.status = :status " +
           "AND o.createdAt BETWEEN :start AND :end " +
           "GROUP BY p.id, p.name " +
           "ORDER BY revenue DESC")
    List<Object[]> findTopProductsByRevenue(@Param("status") Orders.OrderStatus status,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    /**
     * Lấy khách hàng top theo tổng chi tiêu (chỉ đơn hàng đã giao)
     */
    @Query("SELECT u.id, u.fullName, u.email, " +
           "COUNT(DISTINCT o.id) as orderCount, " +
           "COALESCE(SUM(oi.qty * oi.unitPrice), 0) as totalSpent " +
           "FROM Orders o " +
           "JOIN o.user u " +
           "LEFT JOIN o.orderItems oi " +
           "WHERE o.createdAt BETWEEN :start AND :end " +
           "AND o.status = :status " +
           "GROUP BY u.id, u.fullName, u.email " +
           "ORDER BY totalSpent DESC")
    List<Object[]> findTopCustomers(@Param("status") Orders.OrderStatus status,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    /**
     * Lấy doanh thu theo danh mục (cú pháp SQL Server)
     */
    @Query(value = "SELECT c.name, " +
           "COALESCE(SUM(oi.qty * oi.unit_price), 0) as revenue, " +
           "COUNT(DISTINCT o.id) as orderCount " +
           "FROM Category c " +
           "LEFT JOIN Product p ON p.category_id = c.id " +
           "LEFT JOIN ProductVariant pv ON pv.product_id = p.id " +
           "LEFT JOIN OrderItems oi ON oi.variant_id = pv.id " +
           "LEFT JOIN Orders o ON o.id = oi.order_id " +
           "WHERE c.type = N'CATEGORY' " +
           "AND o.status = :status " +
           "AND o.created_at BETWEEN :start AND :end " +
           "GROUP BY c.id, c.name " +
           "HAVING COALESCE(SUM(oi.qty * oi.unit_price), 0) > 0 " +
           "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> findRevenueByCategoryNative(@Param("status") String status,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}

