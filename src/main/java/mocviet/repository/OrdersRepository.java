package mocviet.repository;

import mocviet.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    
    /**
     * Count orders by status
     */
    long countByStatus(Orders.OrderStatus status);
    
    /**
     * Find orders by date range
     */
    List<Orders> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Calculate total revenue from delivered orders
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
     * Count total orders
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createdAt BETWEEN :start AND :end")
    long countOrdersInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Calculate average order value
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
     * Get revenue by date (for chart) - SQL Server syntax
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
     * Get count of distinct customers who placed orders
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) " +
           "FROM Orders o " +
           "WHERE o.createdAt BETWEEN :start AND :end")
    long countDistinctCustomers(@Param("start") LocalDateTime start, 
                               @Param("end") LocalDateTime end);
    
    /**
     * Count orders by status within date range
     */
    @Query("SELECT COUNT(o) FROM Orders o " +
           "WHERE o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByStatusInPeriod(@Param("status") Orders.OrderStatus status,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);
    
    /**
     * Get top products by revenue
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
     * Get top customers by total spent (only DELIVERED orders)
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
     * Get revenue by category (native SQL for SQL Server)
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

