package mocviet.repository;

import mocviet.entity.OTP;
import mocviet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    
    Optional<OTP> findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
            User user, String code, String purpose, LocalDateTime currentTime);
    
    Optional<OTP> findByUserAndCodeAndPurpose(User user, String code, String purpose);
    
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiresAt < :currentTime")
    int deleteExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);
    
    void deleteByUserAndPurpose(User user, String purpose);
}

