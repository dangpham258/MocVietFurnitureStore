package mocviet.service.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ForgotPasswordRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.ResetPasswordRequest;
import mocviet.dto.VerifyOTPRequest;
import mocviet.entity.OTP;
import mocviet.entity.User;
import mocviet.repository.OTPRepository;
import mocviet.repository.UserRepository;
import mocviet.service.IPasswordResetService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements IPasswordResetService {
    
    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    
    private static final String PURPOSE_RESET_PASSWORD = "RESET_PASSWORD";
    private static final int OTP_EXPIRY_MINUTES = 5; // OTP hết hạn sau 5 phút
    private static final int OTP_LENGTH = 6;
    
    @Override
    @Transactional
    public MessageResponse sendResetPasswordOTP(ForgotPasswordRequest request) {
        // Kiểm tra email có tồn tại trong hệ thống không
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return MessageResponse.error("Email chưa tồn tại trong hệ thống");
        }
        
        User user = userOpt.get();
        
        // Kiểm tra user có đang active không
        if (!user.getIsActive()) {
            return MessageResponse.error("Tài khoản đã bị khóa");
        }
        
        // Xóa các OTP cũ của user này với purpose RESET_PASSWORD
        otpRepository.deleteByUserAndPurpose(user, PURPOSE_RESET_PASSWORD);
        
        // Tạo mã OTP mới
        String otpCode = generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Lưu OTP vào database
        OTP otp = new OTP();
        otp.setUser(user);
        otp.setCode(otpCode);
        otp.setPurpose(PURPOSE_RESET_PASSWORD);
        otp.setIsUsed(false);
        otp.setExpiresAt(expiresAt);
        otp.setCreatedAt(LocalDateTime.now());
        
        otpRepository.save(otp);
        
        // Gửi email chứa mã OTP
        try {
            sendOTPEmail(user.getEmail(), user.getFullName(), otpCode);
            return MessageResponse.success("Mã OTP đã được gửi đến email của bạn");
        } catch (Exception e) {
            // Nếu gửi email thất bại, xóa OTP đã tạo
            otpRepository.delete(otp);
            return MessageResponse.error("Không thể gửi email. Vui lòng thử lại sau");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public MessageResponse verifyOTP(VerifyOTPRequest request) {
        // Tìm user theo email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return MessageResponse.error("Email không tồn tại");
        }
        
        User user = userOpt.get();
        
        // Tìm OTP hợp lệ (chưa sử dụng và chưa hết hạn)
        Optional<OTP> otpOpt = otpRepository.findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                user, request.getOtpCode(), PURPOSE_RESET_PASSWORD, LocalDateTime.now());
        
        if (otpOpt.isPresent()) {
            return MessageResponse.success("Mã OTP hợp lệ");
        }
        
        // Kiểm tra các trường hợp lỗi cụ thể
        Optional<OTP> expiredOtpOpt = otpRepository.findByUserAndCodeAndPurpose(
                user, request.getOtpCode(), PURPOSE_RESET_PASSWORD);
        
        if (expiredOtpOpt.isPresent()) {
            OTP otp = expiredOtpOpt.get();
            if (otp.getIsUsed()) {
                return MessageResponse.error("Mã OTP đã được sử dụng. Vui lòng yêu cầu mã mới");
            } else if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                return MessageResponse.error("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới");
            }
        }
        
        return MessageResponse.error("Mã OTP không chính xác");
    }
    
    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        // Validate mật khẩu mới và xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return MessageResponse.error("Mật khẩu không khớp");
        }
        
        // Tìm user theo email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return MessageResponse.error("Email không tồn tại");
        }
        
        User user = userOpt.get();
        
        // Xác thực OTP một lần nữa với thông báo lỗi chi tiết
        Optional<OTP> otpOpt = otpRepository.findByUserAndCodeAndPurposeAndIsUsedFalseAndExpiresAtAfter(
                user, request.getOtpCode(), PURPOSE_RESET_PASSWORD, LocalDateTime.now());
        
        if (otpOpt.isEmpty()) {
            // Kiểm tra các trường hợp lỗi cụ thể
            Optional<OTP> expiredOtpOpt = otpRepository.findByUserAndCodeAndPurpose(
                    user, request.getOtpCode(), PURPOSE_RESET_PASSWORD);
            
            if (expiredOtpOpt.isPresent()) {
                OTP otp = expiredOtpOpt.get();
                if (otp.getIsUsed()) {
                    return MessageResponse.error("Mã OTP đã được sử dụng. Vui lòng yêu cầu mã mới");
                } else if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                    return MessageResponse.error("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới");
                }
            }
            return MessageResponse.error("Mã OTP không chính xác");
        }
        
        OTP otp = otpOpt.get();
        
        // Cập nhật mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Đánh dấu OTP đã sử dụng
        otp.setIsUsed(true);
        otpRepository.save(otp);
        
        return MessageResponse.success("Đã khôi phục mật khẩu thành công");
    }
    
    @Override
    @Transactional
    public MessageResponse resendOTP(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);
        return sendResetPasswordOTP(request);
    }
    
    /**
     * Tạo mã OTP ngẫu nhiên
     */
    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
    /**
     * Gửi email chứa mã OTP
     */
    private void sendOTPEmail(String email, String fullName, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Mã OTP khôi phục mật khẩu - Mộc Việt");
        
        String emailContent = String.format("""
            Xin chào %s,
            
            Chúng tôi nhận được yêu cầu thiết lập lại mật khẩu cho tài khoản của bạn trên hệ thống Mộc Việt Furniture Store của chúng tôi.
            
            Mã OTP của bạn là: %s
            
            Mã này sẽ hết hạn sau %d phút. LƯU Ý: KHÔNG chia sẻ mã này với bất kỳ ai.
            
            Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này.
            
            Trân trọng,
            Đội ngũ Mộc Việt
            """, fullName, otpCode, OTP_EXPIRY_MINUTES);
        
        message.setText(emailContent);
        mailSender.send(message);
    }
}
