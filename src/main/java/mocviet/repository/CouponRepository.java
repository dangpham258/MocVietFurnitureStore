package mocviet.repository;

import mocviet.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    Optional<Coupon> findByCodeAndActiveTrue(String code);
    
    List<Coupon> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        LocalDateTime now, LocalDateTime now2);
    
    List<Coupon> findByActiveTrue();
}
