package mocviet.service.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi email OTP cho cập nhật thông tin cá nhân
     */
    public void sendProfileUpdateOTP(String toEmail, String otpCode, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Mã OTP xác thực cập nhật thông tin cá nhân - Mộc Việt");

            String emailContent = String.format("""
                Xin chào %s,

                Bạn đang thực hiện cập nhật thông tin cá nhân trên hệ thống Mộc Việt.

                Mã OTP xác thực của bạn là: %s

                Mã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.

                Nếu bạn không thực hiện thao tác này, vui lòng bỏ qua email này.

                Trân trọng,
                Đội ngũ Mộc Việt Furniture Store
                """, fullName, otpCode);

            message.setText(emailContent);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }

    /**
     * Gửi email OTP cho đổi mật khẩu
     */
    public void sendPasswordChangeOTP(String toEmail, String otpCode, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Mã OTP xác thực đổi mật khẩu - Mộc Việt");

            String emailContent = String.format("""
                Xin chào %s,

                Bạn đang thực hiện đổi mật khẩu trên hệ thống Mộc Việt.

                Mã OTP xác thực của bạn là: %s

                Mã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.

                Nếu bạn không thực hiện thao tác này, vui lòng bỏ qua email này và kiểm tra bảo mật tài khoản.

                Trân trọng,
                Đội ngũ Mộc Việt Furniture Store
                """, fullName, otpCode);

            message.setText(emailContent);

            mailSender.send(message);
            log.info("Password change OTP email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password change OTP email to: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }
}
