package mocviet.service.customer;

import mocviet.dto.customer.ForgotPasswordRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.customer.ResetPasswordRequest;
import mocviet.dto.customer.VerifyOTPRequest;

public interface IPasswordResetService {
    
    /**
     * Gửi mã OTP qua email để khôi phục mật khẩu
     * @param request Thông tin email
     * @return Kết quả gửi OTP
     */
    MessageResponse sendResetPasswordOTP(ForgotPasswordRequest request);
    
    /**
     * Xác thực mã OTP
     * @param request Thông tin OTP và email
     * @return Kết quả xác thực
     */
    MessageResponse verifyOTP(VerifyOTPRequest request);
    
    /**
     * Đặt lại mật khẩu mới
     * @param request Thông tin mật khẩu mới và OTP
     * @return Kết quả đặt lại mật khẩu
     */
    MessageResponse resetPassword(ResetPasswordRequest request);
    
    /**
     * Gửi lại mã OTP (resend)
     * @param email Email của người dùng
     * @return Kết quả gửi lại OTP
     */
    MessageResponse resendOTP(String email);
}
