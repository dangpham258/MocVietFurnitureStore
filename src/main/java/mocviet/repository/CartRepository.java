package mocviet.repository;

import mocviet.entity.Cart;
import mocviet.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    /**
     * Tìm giỏ hàng theo user ID
     */
    Optional<Cart> findByUserId(Integer userId);
    
    /**
     * Kiểm tra user đã có giỏ hàng chưa
     */
    boolean existsByUserId(Integer userId);
}
