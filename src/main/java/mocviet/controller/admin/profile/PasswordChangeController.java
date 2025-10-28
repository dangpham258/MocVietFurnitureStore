package mocviet.controller.admin.profile;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.PasswordChangeOTPRequest;
import mocviet.dto.admin.PasswordChangeRequest;
import mocviet.entity.OTP;
import mocviet.entity.User;
import mocviet.service.admin.AdminProfileService;
import mocviet.service.admin.OTPService;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class PasswordChangeController {

    private final AdminProfileService profileService;
    private final OTPService otpService;

    /**
     * Gửi OTP để xác thực đổi mật khẩu
     */
    @PostMapping("/send-password-otp")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> sendPasswordChangeOTP(@Valid @RequestBody PasswordChangeOTPRequest request,
                                                   BindingResult bindingResult,
                                                   Authentication authentication) {
        // Kiểm tra validation errors từ DTO trước khi gửi OTP
        if (bindingResult.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);

            // Lấy error đầu tiên
            String firstError = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("Dữ liệu không hợp lệ");

            response.put("message", firstError);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            User currentUser = (User) authentication.getPrincipal();
            otpService.generateAndSendPasswordChangeOTP(currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mã OTP đã được gửi đến email của bạn");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể gửi mã OTP: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                           BindingResult bindingResult,
                                           Authentication authentication,
                                           HttpServletRequest httpRequest,
                                           HttpServletResponse httpResponse) {

        if (bindingResult.hasErrors()) {
            // Lấy tất cả error messages
            String allErrors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((error1, error2) -> error1 + "<br>" + error2)
                    .orElse("Dữ liệu không hợp lệ");

            // Tạo map errors cho từng field
            Map<String, String> fieldErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            });

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", allErrors);
            response.put("errors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            User currentUser = (User) authentication.getPrincipal();

            // Xác thực OTP trước khi đổi mật khẩu
            boolean isOTPValid = otpService.verifyPasswordChangeOTP(currentUser, request.getOtpCode().trim());
            if (!isOTPValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Mã OTP không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.badRequest().body(response);
            }

            // Đổi mật khẩu sau khi xác thực OTP thành công
            profileService.changePassword(currentUser, request);

            // Logout user sau khi đổi mật khẩu thành công
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(httpRequest, httpResponse, authentication);

            // Xóa JWT cookie nếu có (remember me)
            Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0); // Xóa cookie ngay lập tức
            httpResponse.addCookie(jwtCookie);

            System.out.println("JWT cookie cleared after password change");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
            response.put("redirect", "/admin/login");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đổi mật khẩu thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Hủy OTP đổi mật khẩu hiện tại
     */
    @PostMapping("/cancel-password-otp")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> cancelPasswordOTP(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            // Xóa OTP cũ
            otpService.cleanupOldOTPs(currentUser, OTP.Purpose.RESET_PASSWORD);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP đã được hủy thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể hủy OTP: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
