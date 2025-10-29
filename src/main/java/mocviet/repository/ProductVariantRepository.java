package mocviet.repository;

import mocviet.entity.ProductVariant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
    
    List<ProductVariant> findByProductIdAndIsActiveTrue(Integer productId);
    
    boolean existsBySku(String sku);
    
    boolean existsBySkuAndIdNot(String sku, Integer id);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.color.id = :colorId AND pv.typeName = :typeName")
    Optional<ProductVariant> findByProductAndColorAndType(@Param("productId") Integer productId, @Param("colorId") Integer colorId, @Param("typeName") String typeName);
    
    @Query("SELECT pv FROM ProductVariant pv LEFT JOIN FETCH pv.color WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductIdWithColor(@Param("productId") Integer productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQty <= :threshold")
    List<ProductVariant> findLowStockVariants(@Param("threshold") Integer threshold);
    
    List<ProductVariant> findByStockQtyLessThanAndIsActive(Integer stockQty, Boolean isActive);

    /**
     * Lấy danh sách ProductVariant đang active theo product ID
     */
    @EntityGraph(attributePaths = {"color"})
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.isActive = true")
    List<ProductVariant> findActiveByProductId(@Param("productId") Integer productId);

    /**
     * Kiểm tra tồn kho của ProductVariant
     */
    @EntityGraph(attributePaths = {"color"}) // <<< THÊM DÒNG NÀY
    @Query("SELECT pv.stockQty FROM ProductVariant pv WHERE pv.id = :variantId")
    Integer getStockQtyById(@Param("variantId") Integer variantId);

    /**
     * Lấy các biến thể active có discount > 0, sắp xếp theo discount giảm dần
     * Join fetch product để lấy product ID mà không cần query thêm.
     */
    @Query("SELECT pv FROM ProductVariant pv JOIN FETCH pv.product p " +
           "WHERE pv.isActive = true AND pv.discountPercent IS NOT NULL AND pv.discountPercent > 0 AND p.isActive = true " + // Thêm IS NOT NULL
           "ORDER BY pv.discountPercent DESC, pv.salePrice ASC") // Sắp xếp phụ theo giá bán tăng dần
    List<ProductVariant> findTopDiscountedActiveVariants(Pageable pageable);
}
