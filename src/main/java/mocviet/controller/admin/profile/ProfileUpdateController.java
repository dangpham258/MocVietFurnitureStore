package mocviet.controller.admin.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ProfileUpdateRequest;
import mocviet.entity.User;
import mocviet.service.admin.AdminProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileUpdateController {
    
    private final AdminProfileService profileService;
    
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
}
