package mocviet.repository;

import mocviet.entity.Product;
import mocviet.entity.User;
import mocviet.entity.Wishlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    
    /**
     * Tìm wishlist theo user và product
     */
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    
    /**
     * Tìm wishlist theo user với eager fetch product và category
     * Note: Không thể fetch nhiều collection cùng lúc (MultipleBagFetchException)
     */
    @EntityGraph(attributePaths = {"product", "product.category", "product.collection"})
    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Đếm số lượng wishlist của user
     */
    Long countByUser(User user);
    
    /**
     * Kiểm tra sản phẩm đã có trong wishlist chưa
     */
    boolean existsByUserAndProduct(User user, Product product);
    
    /**
     * Xóa wishlist theo user và product
     */
    void deleteByUserAndProduct(User user, Product product);
    
    /**
     * Lấy danh sách product_id trong wishlist của user
     */
    @Query("SELECT w.product.id FROM Wishlist w WHERE w.user = :user")
    List<Integer> findProductIdsByUser(@Param("user") User user);
    
    /**
     * Tìm wishlist theo user và product_id
     */
    Optional<Wishlist> findByUserAndProductId(User user, Integer productId);
}

