package mocviet.repository;

import mocviet.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    
    /**
     * Fetch productImages cho các products
     */
    @EntityGraph(attributePaths = {"productImages"})
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findByIdsWithImages(@Param("productIds") List<Integer> productIds);
    
    /**
     * Tìm product theo slug
     */
    Optional<Product> findBySlug(String slug);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Integer id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection")
    Page<Product> findAllWithCategoryAndCollection(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.name LIKE CONCAT(:keyword, '%') OR p.description LIKE CONCAT(:keyword, '%')")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE (p.name LIKE CONCAT(:keyword, '%') OR p.description LIKE CONCAT(:keyword, '%')) AND p.category.id = :categoryId")
    Page<Product> searchProductsInCategory(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    Page<Product> findByCategoryIdAndActive(@Param("categoryId") Integer categoryId, Pageable pageable);
    
    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.collection WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndCollection(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.stockQty <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.stockQty = 0")
    List<Product> findOutOfStockProducts();
}