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
    
    Optional<ProductVariant> findBySku(String sku);
    
    Boolean existsBySku(String sku);
    
    Boolean existsBySkuAndIdNot(String sku, Integer id);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.color.id = :colorId AND pv.typeName = :typeName")
    Optional<ProductVariant> findByProductAndColorAndType(@Param("productId") Integer productId, 
                                                          @Param("colorId") Integer colorId, 
                                                          @Param("typeName") String typeName);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductId(@Param("productId") Integer productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQty <= :threshold AND pv.isActive = true")
    List<ProductVariant> findLowStockVariants(@Param("threshold") Integer threshold);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQty = 0 AND pv.isActive = true")
    List<ProductVariant> findOutOfStockVariants();
}
