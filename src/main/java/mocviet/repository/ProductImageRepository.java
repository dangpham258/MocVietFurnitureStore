package mocviet.repository;

import mocviet.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    
    /**
     * Tìm ProductImage theo product ID
     */
	@EntityGraph(attributePaths = {"color"})
    List<ProductImage> findByProductId(Integer productId);
    
    /**
     * Tìm ProductImage theo product ID và color ID
     */
    List<ProductImage> findByProductIdAndColorId(Integer productId, Integer colorId);
    
    /**
     * Lấy ảnh đầu tiên của sản phẩm theo màu
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId ORDER BY pi.id ASC")
    List<ProductImage> findFirstByProductIdAndColorId(@Param("productId") Integer productId, @Param("colorId") Integer colorId);
    
    /**
     * Lấy ảnh sản phẩm với thông tin màu
     */
    @Query("SELECT pi FROM ProductImage pi LEFT JOIN FETCH pi.color WHERE pi.product.id = :productId")
    List<ProductImage> findByProductIdWithColor(@Param("productId") Integer productId);
    
    /**
     * Lấy ảnh sản phẩm theo product và color
     */
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId")
    List<ProductImage> findByProductAndColor(@Param("productId") Integer productId, @Param("colorId") Integer colorId);
    
    /**
     * Xóa ảnh theo product ID và color ID
     */
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId AND pi.color.id = :colorId")
    void deleteByProductIdAndColorId(@Param("productId") Integer productId, @Param("colorId") Integer colorId);
    
    /**
     * Xóa tất cả ảnh theo product ID
     */
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
