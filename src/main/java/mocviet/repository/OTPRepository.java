package mocviet.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.OTP;
import mocviet.entity.User;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {

    Optional<OTP> findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
            User user, String code, OTP.Purpose purpose, LocalDateTime currentTime);

    void deleteByExpiresAtBefore(LocalDateTime currentTime);

    void deleteByUserAndPurposeAndIsUsedFalse(User user, OTP.Purpose purpose);
}

