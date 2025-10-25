package mocviet.repository;

import mocviet.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    
    List<Orders> findByUserId(Integer userId);
    
    List<Orders> findByStatus(Orders.OrderStatus status);
    
    List<Orders> findByUserIdAndStatus(Integer userId, Orders.OrderStatus status);
}
