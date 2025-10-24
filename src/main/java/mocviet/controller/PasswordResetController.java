package mocviet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.ForgotPasswordRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.ResetPasswordRequest;
import mocviet.dto.VerifyOTPRequest;
import mocviet.service.customer.IPasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {
    
    private final IPasswordResetService passwordResetService;
    
    /**
     * Hiển thị trang quên mật khẩu
     */
    @GetMapping("/forgot")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }
    
    /**
     * Xử lý yêu cầu quên mật khẩu
     */
    @PostMapping("/forgot")
    public String processForgotPassword(
            @Valid @ModelAttribute ForgotPasswordRequest request,
            BindingResult bindingResult,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        
        MessageResponse response = passwordResetService.sendResetPasswordOTP(request);
        
        if (response.isSuccess()) {
            VerifyOTPRequest verifyRequest = new VerifyOTPRequest();
            verifyRequest.setEmail(request.getEmail());
            model.addAttribute("email", request.getEmail());
            model.addAttribute("verifyOTPRequest", verifyRequest);
            model.addAttribute("successMessage", response.getMessage());
            return "auth/verify-otp";
        } else {
            // Giữ lại giá trị email và hiển thị lỗi trên cùng trang
            model.addAttribute("errorMessage", response.getMessage());
            model.addAttribute("forgotPasswordRequest", request); // Giữ lại form data
            return "auth/forgot-password";
        }
    }
    
    /**
     * Hiển thị trang xác thực OTP
     */
    @GetMapping("/verify-otp")
    public String showVerifyOTPPage(
            @RequestParam String email,
            Model model) {
        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
        verifyOTPRequest.setEmail(email);
        model.addAttribute("verifyOTPRequest", verifyOTPRequest);
        return "auth/verify-otp";
    }
    
    /**
     * Xử lý xác thực OTP
     */
    @PostMapping("/verify-otp")
    public String processVerifyOTP(
            @Valid @ModelAttribute VerifyOTPRequest request,
            BindingResult bindingResult,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "auth/verify-otp";
        }
        
        MessageResponse response = passwordResetService.verifyOTP(request);
        
        if (response.isSuccess()) {
            ResetPasswordRequest resetRequest = new ResetPasswordRequest();
            resetRequest.setEmail(request.getEmail());
            resetRequest.setOtpCode(request.getOtpCode());
            model.addAttribute("resetPasswordRequest", resetRequest);
            model.addAttribute("successMessage", response.getMessage());
            return "auth/reset-password";
        } else {
            // Giữ lại email và hiển thị lỗi trên cùng trang
            model.addAttribute("errorMessage", response.getMessage());
            model.addAttribute("verifyOTPRequest", request); // Giữ lại form data
            return "auth/verify-otp";
        }
    }
    
    /**
     * Hiển thị trang đặt lại mật khẩu
     */
    @GetMapping("/reset")
    public String showResetPasswordPage(
            @RequestParam String email,
            @RequestParam String otpCode,
            Model model) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);
        request.setOtpCode(otpCode);
        model.addAttribute("resetPasswordRequest", request);
        return "auth/reset-password";
    }
    
    /**
     * Xử lý đặt lại mật khẩu
     */
    @PostMapping("/reset")
    public String processResetPassword(
            @Valid @ModelAttribute ResetPasswordRequest request,
            BindingResult bindingResult,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }
        
        MessageResponse response = passwordResetService.resetPassword(request);
        
        if (response.isSuccess()) {
            model.addAttribute("successMessage", response.getMessage());
            return "auth/reset-success";
        } else {
            model.addAttribute("errorMessage", response.getMessage());
            return "auth/reset-password";
        }
    }
    
    /**
     * API endpoint để gửi lại OTP
     */
    @PostMapping("/resend-otp")
    @ResponseBody
    public ResponseEntity<MessageResponse> resendOTP(@RequestParam String email) {
        MessageResponse response = passwordResetService.resendOTP(email);
        return ResponseEntity.ok(response);
    }
    
    /**
     * API endpoint để kiểm tra OTP (AJAX)
     */
    @PostMapping("/check-otp")
    @ResponseBody
    public ResponseEntity<MessageResponse> checkOTP(@RequestBody VerifyOTPRequest request) {
        MessageResponse response = passwordResetService.verifyOTP(request);
        return ResponseEntity.ok(response);
    }
}
