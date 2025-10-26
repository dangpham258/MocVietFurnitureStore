package mocviet.controller.admin.users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.UserCreateRequest;
import mocviet.dto.admin.UserResponse;
import mocviet.service.admin.AdminUserService;

@RestController
@RequestMapping("/admin/users/api")
@RequiredArgsConstructor
public class UsersApiController {
    
    private final AdminUserService adminUserService;
    
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        UserResponse user = adminUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        List<UserResponse> users = adminUserService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse user = adminUserService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        
        Map<String, String> errors = new HashMap<>();
        List<String> errorMessages = new java.util.ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();
            errors.put(fieldName, errorMsg);
            errorMessages.add(errorMsg);
        });
        
        // Sử dụng message đầu tiên làm message chính
        String mainMessage = errorMessages.isEmpty() ? "Validation failed" : errorMessages.get(0);
        response.put("message", mainMessage);
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Integer id) {
        try {
            adminUserService.toggleUserStatus(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

