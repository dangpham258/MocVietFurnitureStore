package mocviet.repository;

import mocviet.entity.Product;
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
}