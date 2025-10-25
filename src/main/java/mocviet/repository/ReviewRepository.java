package mocviet.repository;

import mocviet.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    long countByCreatedAtAfter(LocalDateTime dateTime);
}
