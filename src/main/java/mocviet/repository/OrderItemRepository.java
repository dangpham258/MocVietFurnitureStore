package mocviet.repository;

import mocviet.entity.OrderItem;
import mocviet.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    boolean existsByVariantId(Integer variantId);
    
    List<OrderItem> findByOrder(Orders order);
}
