package mocviet.repository;

import mocviet.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    boolean existsByVariantId(Integer variantId);
}
