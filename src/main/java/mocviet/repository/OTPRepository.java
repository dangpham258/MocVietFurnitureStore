package mocviet.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocviet.entity.OTP;
import mocviet.entity.User;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {

    Optional<OTP> findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
            User user, String code, OTP.Purpose purpose, LocalDateTime currentTime);
    
    Optional<OTP> findByUserAndCodeAndPurpose(User user, String code, OTP.Purpose purpose);
    
    @Modifying
    @Query("DELETE FROM OTP o WHERE o.expiresAt < :currentTime")
    int deleteExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByExpiresAtBefore(LocalDateTime currentTime);

    void deleteByUserAndPurposeAndIsUsedFalse(User user, OTP.Purpose purpose);

    void deleteByUserAndPurpose(User user, OTP.Purpose purpose);
}

