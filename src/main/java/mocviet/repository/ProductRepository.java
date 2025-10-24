package mocviet.repository;

import mocviet.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection")
    Page<Product> findAllWithCategoryAndCollection(Pageable pageable);
    
    Optional<Product> findBySlug(String slug);
    
    Boolean existsByName(String name);
    
    Boolean existsBySlug(String slug);
    
    Boolean existsByNameAndIdNot(String name, Integer id);
    
    Boolean existsBySlugAndIdNot(String slug, Integer id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.isActive = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryIdAndActive(@Param("categoryId") Integer categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.isActive = :isActive")
    Page<Product> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.name LIKE CONCAT('%', :keyword, '%') OR p.description LIKE CONCAT('%', :keyword, '%')")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.stockQty <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.stockQty = 0 AND p.isActive = true")
    List<Product> findOutOfStockProducts();
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndCollection(@Param("id") Integer id);
}