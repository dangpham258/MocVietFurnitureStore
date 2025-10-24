package mocviet.repository;

import mocviet.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
    boolean existsByVariantId(Integer variantId);
}
