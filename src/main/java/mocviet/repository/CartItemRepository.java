package mocviet.repository;

import mocviet.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    
    /**
     * Tìm CartItem theo cart ID và variant ID
     */
    Optional<CartItem> findByCartIdAndVariantId(Integer cartId, Integer variantId);
    
    /**
     * Lấy danh sách CartItem theo cart ID, sắp xếp theo ID tăng dần
     */
    List<CartItem> findByCartIdOrderByIdAsc(Integer cartId);
    
    
    /**
     * Đếm số lượng CartItem trong giỏ hàng
     */
    int countByCartId(Integer cartId);
    
    /**
     * Xóa tất cả CartItem theo cart ID
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Integer cartId);
    
    /**
     * Lấy danh sách CartItem theo danh sách ID
     */
    List<CartItem> findAllById(Iterable<Integer> ids);
}
