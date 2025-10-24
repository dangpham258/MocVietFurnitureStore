package mocviet.repository;

import mocviet.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId")
    List<ProductImage> findByProductId(@Param("productId") Integer productId);
    
    @Query("SELECT pi FROM ProductImage pi LEFT JOIN FETCH pi.color WHERE pi.product.id = :productId")
    List<ProductImage> findByProductIdWithColor(@Param("productId") Integer productId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId")
    List<ProductImage> findByProductAndColor(@Param("productId") Integer productId, @Param("colorId") Integer colorId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.color.id = :colorId")
    List<ProductImage> findByColorId(@Param("colorId") Integer colorId);
    
    void deleteByProductId(Integer productId);
    
    void deleteByProductIdAndColorId(Integer productId, Integer colorId);
}
