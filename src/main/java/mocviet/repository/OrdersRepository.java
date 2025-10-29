package mocviet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocviet.entity.Orders;

import mocviet.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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


@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    
    List<Orders> findByUserId(Integer userId);
    
    List<Orders> findByStatus(Orders.OrderStatus status);
    
    List<Orders> findByUserIdAndStatus(Integer userId, Orders.OrderStatus status);
    
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL")
    Page<Orders> findByStatusAndOrderDeliveryIsNull(@Param("status") Orders.OrderStatus status, Pageable pageable);
    
    // Query để sort pending orders theo tổng tiền
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL ORDER BY " +
           "(SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) DESC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullOrderByTotalAmountDesc(@Param("status") Orders.OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL ORDER BY " +
           "(SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) ASC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullOrderByTotalAmountAsc(@Param("status") Orders.OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.address.city IN " +
           "(SELECT pz.provinceName FROM ProvinceZone pz WHERE pz.zone.id = :zoneId)")
    List<Orders> findByStatusAndAddressCityInZone(@Param("status") Orders.OrderStatus status, @Param("zoneId") Integer zoneId);
    
    // Query kết hợp zone và keyword
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
           "o.address.city IN (SELECT pz.provinceName FROM ProvinceZone pz WHERE pz.zone.id = :zoneId) AND " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
    Page<Orders> findByStatusAndOrderDeliveryIsNullAndZoneAndKeyword(@Param("status") Orders.OrderStatus status,
                                                                     @Param("zoneId") Integer zoneId,
                                                                     @Param("keyword") String keyword,
                                                                     Pageable pageable);
    
    // Query kết hợp zone và keyword với sort theo tổng tiền
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
           "o.address.city IN (SELECT pz.provinceName FROM ProvinceZone pz WHERE pz.zone.id = :zoneId) AND " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) DESC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullAndZoneAndKeywordOrderByTotalAmountDesc(@Param("status") Orders.OrderStatus status,
                                                                                           @Param("zoneId") Integer zoneId,
                                                                                           @Param("keyword") String keyword,
                                                                                           Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
           "o.address.city IN (SELECT pz.provinceName FROM ProvinceZone pz WHERE pz.zone.id = :zoneId) AND " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) ASC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullAndZoneAndKeywordOrderByTotalAmountAsc(@Param("status") Orders.OrderStatus status,
                                                                                          @Param("zoneId") Integer zoneId,
                                                                                          @Param("keyword") String keyword,
                                                                                          Pageable pageable);
    
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
               "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
        Page<Orders> findByStatusAndOrderDeliveryIsNullAndKeyword(@Param("status") Orders.OrderStatus status,
                                                                 @Param("keyword") String keyword,
                                                                 Pageable pageable);
    
    // Query pending orders với keyword và sort theo tổng tiền
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) DESC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullAndKeywordOrderByTotalAmountDesc(@Param("status") Orders.OrderStatus status,
                                                                                   @Param("keyword") String keyword,
                                                                                   Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.orderDelivery IS NULL AND " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) ASC")
    Page<Orders> findByStatusAndOrderDeliveryIsNullAndKeywordOrderByTotalAmountAsc(@Param("status") Orders.OrderStatus status,
                                                                                  @Param("keyword") String keyword,
                                                                                  Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
    Page<Orders> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Query để sort theo tổng tiền thực tế (tính từ order items + shipping fee)
    @Query("SELECT o FROM Orders o ORDER BY " +
           "(SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) DESC")
    Page<Orders> findAllOrderByTotalAmountDesc(Pageable pageable);
    
    // Native query để sort theo tổng tiền - KHÔNG sử dụng Pageable sort
    @Query(value = "SELECT o.* FROM Orders o ORDER BY " +
                   "(SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) DESC", 
           nativeQuery = true)
    List<Orders> findAllOrderByTotalAmountDescNative();
    
    @Query(value = "SELECT o.* FROM Orders o ORDER BY " +
                   "(SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) ASC", 
           nativeQuery = true)
    List<Orders> findAllOrderByTotalAmountAscNative();
    
    // Native query cho pending orders - KHÔNG sử dụng Pageable sort
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) DESC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersByTotalAmountDescNative();
    
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) ASC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersByTotalAmountAscNative();
    
    // Native query cho pending orders với keyword
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) AND " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) DESC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersWithKeywordByTotalAmountDescNative(@Param("keyword") String keyword);
    
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) AND " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) ASC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersWithKeywordByTotalAmountAscNative(@Param("keyword") String keyword);
    
    // Native query cho pending orders với zone và keyword
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) AND " +
                   "o.address_id IN (SELECT a.id FROM Address a WHERE a.city IN " +
                   "(SELECT pz.province_name FROM ProvinceZone pz WHERE pz.zone_id = :zoneId)) AND " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) DESC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersWithZoneAndKeywordByTotalAmountDescNative(@Param("zoneId") Integer zoneId, @Param("keyword") String keyword);
    
    @Query(value = "SELECT o.* FROM Orders o WHERE o.status = 'CONFIRMED' AND o.id NOT IN " +
                   "(SELECT od.order_id FROM OrderDelivery od WHERE od.order_id IS NOT NULL) AND " +
                   "o.address_id IN (SELECT a.id FROM Address a WHERE a.city IN " +
                   "(SELECT pz.province_name FROM ProvinceZone pz WHERE pz.zone_id = :zoneId)) AND " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) ASC", 
           nativeQuery = true)
    List<Orders> findPendingOrdersWithZoneAndKeywordByTotalAmountAscNative(@Param("zoneId") Integer zoneId, @Param("keyword") String keyword);
    
    // Native query cho tất cả orders với keyword
    @Query(value = "SELECT o.* FROM Orders o WHERE " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) DESC", 
           nativeQuery = true)
    List<Orders> findAllOrdersWithKeywordByTotalAmountDescNative(@Param("keyword") String keyword);
    
    @Query(value = "SELECT o.* FROM Orders o WHERE " +
                   "(CAST(o.id AS NVARCHAR(20)) LIKE :keyword OR o.user_id IN " +
                   "(SELECT u.id FROM Users u WHERE u.full_name LIKE :keyword)) " +
                   "ORDER BY (SELECT COALESCE(SUM(oi.unit_price * oi.qty), 0) FROM OrderItems oi WHERE oi.order_id = o.id) + COALESCE(o.shipping_fee, 0) ASC", 
           nativeQuery = true)
    List<Orders> findAllOrdersWithKeywordByTotalAmountAscNative(@Param("keyword") String keyword);
    
    @Query("SELECT o FROM Orders o ORDER BY " +
           "(SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) ASC")
    Page<Orders> findAllOrderByTotalAmountAsc(Pageable pageable);
    
    // Query với keyword và sort theo tổng tiền
    @Query("SELECT o FROM Orders o WHERE " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) DESC")
    Page<Orders> findByKeywordOrderByTotalAmountDesc(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT o FROM Orders o WHERE " +
           "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%) " +
           "ORDER BY (SELECT COALESCE(SUM(oi.unitPrice * oi.qty), 0) FROM OrderItem oi WHERE oi.order = o) + COALESCE(o.shippingFee, 0) ASC")
    Page<Orders> findByKeywordOrderByTotalAmountAsc(@Param("keyword") String keyword, Pageable pageable);

        long countByCreatedAtAfter(LocalDateTime dateTime);
        
        long countByStatusAndOrderDeliveryIsNull(Orders.OrderStatus status);
        
        List<Orders> findByCreatedAtAfterAndStatus(LocalDateTime dateTime, Orders.OrderStatus status);
        
        List<Orders> findTop10ByOrderByCreatedAtDesc();
        
        // Additional methods for order management
        Page<Orders> findByStatus(Orders.OrderStatus status, Pageable pageable);
        
        Page<Orders> findByReturnStatus(Orders.ReturnStatus returnStatus, Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND " +
               "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
        Page<Orders> findByStatusAndKeyword(@Param("status") Orders.OrderStatus status, 
                                           @Param("keyword") String keyword, 
                                           Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.createdAt BETWEEN :fromDate AND :toDate")
        Page<Orders> findByStatusAndCreatedAtBetween(@Param("status") Orders.OrderStatus status,
                                                    @Param("fromDate") LocalDateTime fromDate,
                                                    @Param("toDate") LocalDateTime toDate,
                                                    Pageable pageable);
        
        // Query cho đơn hàng hoàn thành (DELIVERED và không trong quy trình trả hàng)
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND " +
               "(o.returnStatus IS NULL OR o.returnStatus = :rejectedStatus)")
        Page<Orders> findCompletedOrders(@Param("status") Orders.OrderStatus status,
                                         @Param("rejectedStatus") Orders.ReturnStatus rejectedStatus,
                                         Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND " +
               "(o.returnStatus IS NULL OR o.returnStatus = :rejectedStatus) AND " +
               "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
        Page<Orders> findCompletedOrdersWithKeyword(@Param("status") Orders.OrderStatus status,
                                                    @Param("rejectedStatus") Orders.ReturnStatus rejectedStatus,
                                                    @Param("keyword") String keyword,
                                                    Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND " +
               "(o.returnStatus IS NULL OR o.returnStatus = :rejectedStatus) AND " +
               "o.createdAt BETWEEN :fromDate AND :toDate")
        Page<Orders> findCompletedOrdersWithDateRange(@Param("status") Orders.OrderStatus status,
                                                      @Param("rejectedStatus") Orders.ReturnStatus rejectedStatus,
                                                      @Param("fromDate") LocalDateTime fromDate,
                                                      @Param("toDate") LocalDateTime toDate,
                                                      Pageable pageable);
        
        // Query cho đơn hàng đã hoàn trả thành công
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.returnStatus = :returnStatus")
        Page<Orders> findReturnedOrders(@Param("status") Orders.OrderStatus status,
                                        @Param("returnStatus") Orders.ReturnStatus returnStatus,
                                        Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.returnStatus = :returnStatus AND " +
               "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
        Page<Orders> findReturnedOrdersWithKeyword(@Param("status") Orders.OrderStatus status,
                                                   @Param("returnStatus") Orders.ReturnStatus returnStatus,
                                                   @Param("keyword") String keyword,
                                                   Pageable pageable);
        
        // Query cho đơn hàng đang giao (CONFIRMED hoặc DISPATCHED)
        @Query("SELECT o FROM Orders o WHERE o.status IN :statuses")
        Page<Orders> findByStatusIn(@Param("statuses") List<Orders.OrderStatus> statuses, Pageable pageable);
        
        @Query("SELECT o FROM Orders o WHERE o.status IN :statuses AND " +
               "(CAST(o.id AS string) LIKE %:keyword% OR o.user.fullName LIKE %:keyword%)")
        Page<Orders> findByStatusInAndKeyword(@Param("statuses") List<Orders.OrderStatus> statuses,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);
}
