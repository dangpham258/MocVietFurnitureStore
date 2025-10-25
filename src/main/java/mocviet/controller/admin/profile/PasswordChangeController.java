package mocviet.controller.admin.profile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.PasswordChangeRequest;
import mocviet.entity.User;
import mocviet.service.admin.AdminProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class PasswordChangeController {
    
    private final AdminProfileService profileService;
    
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
}
