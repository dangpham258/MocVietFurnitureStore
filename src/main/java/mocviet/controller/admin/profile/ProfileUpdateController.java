package mocviet.controller.admin.profile;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ProfileUpdateOTPRequest;
import mocviet.dto.admin.ProfileUpdateRequest;
import mocviet.entity.OTP;
import mocviet.entity.User;
import mocviet.service.admin.AdminProfileService;
import mocviet.service.admin.OTPService;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileUpdateController {

    private final AdminProfileService profileService;
    private final OTPService otpService;

    /**
     * Gửi OTP để xác thực cập nhật thông tin cá nhân
     */
    @PostMapping("/send-otp")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> sendProfileUpdateOTP(@Valid @RequestBody ProfileUpdateOTPRequest request,
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
            otpService.generateAndSendProfileUpdateOTP(currentUser);

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

    /**
     * Xác thực OTP và cập nhật thông tin cá nhân
     */
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest request,
                                          BindingResult bindingResult,
                                          Authentication authentication) {

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

            // Xác thực OTP trước khi cập nhật
            boolean isOTPValid = otpService.verifyProfileUpdateOTP(currentUser, request.getOtpCode().trim());
            if (!isOTPValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Mã OTP không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.badRequest().body(response);
            }

            // Cập nhật thông tin sau khi xác thực OTP thành công
            profileService.updateProfile(currentUser, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cập nhật thông tin thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Hủy OTP hiện tại
     */
    @PostMapping("/cancel-otp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cancelOTP(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            // Xóa OTP cũ
            otpService.cleanupOldOTPs(currentUser, OTP.Purpose.REGISTER);

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
