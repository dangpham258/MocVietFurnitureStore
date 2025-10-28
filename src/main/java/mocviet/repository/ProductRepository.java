package mocviet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

}
