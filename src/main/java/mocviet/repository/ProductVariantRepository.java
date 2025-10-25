package mocviet.repository;

import mocviet.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    
    /**
     * Tìm ProductVariant theo product ID và color ID
     */
    List<ProductVariant> findByProductIdAndColorId(Integer productId, Integer colorId);
    
    /**
     * Tìm ProductVariant theo product ID
     */
    List<ProductVariant> findByProductId(Integer productId);
    
    /**
     * Tìm ProductVariant theo SKU
     */
    Optional<ProductVariant> findBySku(String sku);
    
    /**
     * Lấy danh sách ProductVariant đang active theo product ID
     */
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true")
    List<ProductVariant> findActiveByProductId(@Param("productId") Integer productId);
    
    /**
     * Kiểm tra tồn kho của ProductVariant
     */
    @Query("SELECT pv.stockQty FROM ProductVariant pv WHERE pv.id = :variantId")
    Integer getStockQtyById(@Param("variantId") Integer variantId);
    
    List<ProductVariant> findByProductIdAndIsActiveTrue(Integer productId);
}
