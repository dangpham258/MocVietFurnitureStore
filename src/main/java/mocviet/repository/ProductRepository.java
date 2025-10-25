package mocviet.repository;

import mocviet.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Thêm import này
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Thêm JpaSpecificationExecutor
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    
    Optional<Product> findBySlug(String slug);
    
    // Các phương thức khác...
}