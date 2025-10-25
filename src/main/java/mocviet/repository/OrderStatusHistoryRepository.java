package mocviet.repository;

import mocviet.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
    
    /**
     * Tìm lịch sử trạng thái theo order ID, sắp xếp theo thời gian giảm dần
     */
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(Integer orderId);
    
    /**
     * Tìm lịch sử trạng thái theo order ID, sắp xếp theo thời gian tăng dần
     */
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Integer orderId);
    
    /**
     * Tìm lịch sử trạng thái theo order ID và status
     */
    List<OrderStatusHistory> findByOrderIdAndStatusOrderByChangedAtDesc(Integer orderId, String status);
}
