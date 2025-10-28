package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    Optional<Coupon> findByCode(String code);
}

