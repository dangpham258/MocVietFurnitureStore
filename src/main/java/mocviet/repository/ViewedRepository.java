package mocviet.repository;

import mocviet.entity.Product;
import mocviet.entity.User;
import mocviet.entity.Viewed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewedRepository extends JpaRepository<Viewed, Integer> {

    Page<Viewed> findByUserOrderByViewedAtDesc(User user, Pageable pageable);

    Optional<Viewed> findByUserAndProduct(User user, Product product);

    long countByUser(User user);
}


