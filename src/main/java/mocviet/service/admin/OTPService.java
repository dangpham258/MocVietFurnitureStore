package mocviet.service.admin;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.entity.OTP;
import mocviet.entity.User;
import mocviet.repository.OTPRepository;

/**
 * Service để quản lý OTP (tạo, verify, cleanup)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {

    private final OTPRepository otpRepository;
    private final EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Tạo và gửi OTP cho cập nhật thông tin cá nhân
     */
    @Transactional
    public void generateAndSendProfileUpdateOTP(User user) {
        try {
            // Xóa các OTP cũ của user cho mục đích REGISTER (dùng cho profile update)
            cleanupOldOTPs(user, OTP.Purpose.REGISTER);

            // Tạo OTP mới
            String otpCode = generateOTPCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

            OTP otp = new OTP();
            otp.setUser(user);
            otp.setCode(otpCode);
            otp.setPurpose(OTP.Purpose.REGISTER); // Dùng REGISTER cho profile update
            otp.setIsUsed(false);
            otp.setExpiresAt(expiresAt);

            otpRepository.save(otp);

            // Gửi email OTP
            emailService.sendProfileUpdateOTP(user.getEmail(), otpCode, user.getFullName());

            log.info("Profile update OTP generated and sent for user: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to generate profile update OTP for user: {}", user.getEmail(), e);
            throw new RuntimeException("Không thể tạo mã OTP", e);
        }
    }

    /**
     * Tạo và gửi OTP cho đổi mật khẩu
     */
    @Transactional
    public void generateAndSendPasswordChangeOTP(User user) {
        try {
            // Xóa các OTP cũ của user cho mục đích RESET_PASSWORD (dùng cho password change)
            cleanupOldOTPs(user, OTP.Purpose.RESET_PASSWORD);

            // Tạo OTP mới
            String otpCode = generateOTPCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

            OTP otp = new OTP();
            otp.setUser(user);
            otp.setCode(otpCode);
            otp.setPurpose(OTP.Purpose.RESET_PASSWORD); // Dùng RESET_PASSWORD cho password change
            otp.setIsUsed(false);
            otp.setExpiresAt(expiresAt);

            otpRepository.save(otp);

            // Gửi email OTP
            emailService.sendPasswordChangeOTP(user.getEmail(), otpCode, user.getFullName());

            log.info("Password change OTP generated and sent for user: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to generate password change OTP for user: {}", user.getEmail(), e);
            throw new RuntimeException("Không thể tạo mã OTP", e);
        }
    }

    /**
     * Xác thực OTP cho cập nhật thông tin cá nhân
     */
    @Transactional
    public boolean verifyProfileUpdateOTP(User user, String otpCode) {
        try {
            Optional<OTP> otpOpt = otpRepository.findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                user, otpCode, OTP.Purpose.REGISTER, LocalDateTime.now()
            );

            if (otpOpt.isPresent()) {
                OTP otp = otpOpt.get();
                otp.setIsUsed(true);
                otpRepository.save(otp);

                log.info("Profile update OTP verified successfully for user: {}", user.getEmail());
                return true;
            }

            // OTP sai hoặc hết hạn - hủy tất cả OTP cũ để tránh brute force
            cleanupOldOTPs(user, OTP.Purpose.REGISTER);

            log.warn("Invalid or expired profile update OTP for user: {}", user.getEmail());
            return false;

        } catch (Exception e) {
            log.error("Failed to verify profile update OTP for user: {}", user.getEmail(), e);
            return false;
        }
    }

    /**
     * Xác thực OTP cho đổi mật khẩu
     */
    @Transactional
    public boolean verifyPasswordChangeOTP(User user, String otpCode) {
        try {
            Optional<OTP> otpOpt = otpRepository.findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                user, otpCode, OTP.Purpose.RESET_PASSWORD, LocalDateTime.now()
            );

            if (otpOpt.isPresent()) {
                OTP otp = otpOpt.get();
                otp.setIsUsed(true);
                otpRepository.save(otp);

                log.info("Password change OTP verified successfully for user: {}", user.getEmail());
                return true;
            }

            // OTP sai hoặc hết hạn - hủy tất cả OTP cũ để tránh brute force
            cleanupOldOTPs(user, OTP.Purpose.RESET_PASSWORD);

            log.warn("Invalid or expired password change OTP for user: {}", user.getEmail());
            return false;

        } catch (Exception e) {
            log.error("Failed to verify password change OTP for user: {}", user.getEmail(), e);
            return false;
        }
    }

    /**
     * Xóa các OTP cũ của user cho một mục đích cụ thể
     */
    @Transactional
    public void cleanupOldOTPs(User user, OTP.Purpose purpose) {
        try {
            // Xóa các OTP cũ chưa sử dụng của user cho mục đích này
            otpRepository.deleteByUserAndPurposeAndIsUsedFalse(user, purpose);
            log.debug("Cleaned up old OTPs for user: {} purpose: {}", user.getEmail(), purpose);
        } catch (Exception e) {
            log.error("Failed to cleanup old OTPs for user: {} purpose: {}", user.getEmail(), purpose, e);
        }
    }

    /**
     * Tạo mã OTP ngẫu nhiên
     */
    private String generateOTPCode() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    /**
     * Scheduled task để xóa các OTP đã hết hạn (chạy mỗi 5 phút)
     */
    @Scheduled(fixedRate = 300000) // 5 phút
    @Async
    public void cleanupExpiredOTPs() {
        try {
            LocalDateTime now = LocalDateTime.now();
            otpRepository.deleteByExpiresAtBefore(now);
            log.info("Cleaned up expired OTPs");
        } catch (Exception e) {
            log.error("Failed to cleanup expired OTPs", e);
        }
    }
}
