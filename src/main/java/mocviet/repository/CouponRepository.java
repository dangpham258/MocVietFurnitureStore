package mocviet.repository;

import mocviet.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    /**
     * Validate coupon code với điều kiện thời gian và số tiền tối thiểu
     */
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.active = true " +
           "AND :currentDate BETWEEN c.startDate AND c.endDate " +
           "AND :subtotal >= c.minOrderAmount")
    Optional<Coupon> findValidCoupon(@Param("code") String code, 
                                     @Param("currentDate") LocalDateTime currentDate, 
                                     @Param("subtotal") java.math.BigDecimal subtotal);
}

