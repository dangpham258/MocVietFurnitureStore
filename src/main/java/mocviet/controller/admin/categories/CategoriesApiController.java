package mocviet.controller.admin.categories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.CategoryCreateRequest;
import mocviet.dto.admin.CategoryResponse;
import mocviet.dto.admin.CategoryUpdateRequest;
import mocviet.service.admin.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories/api")
@RequiredArgsConstructor
public class CategoriesApiController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = adminCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer id) {
        CategoryResponse category = adminCategoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        List<CategoryResponse> categories = adminCategoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        try {
            CategoryResponse category = adminCategoryService.createCategory(request);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryUpdateRequest request) {
        try {
            CategoryResponse category = adminCategoryService.updateCategory(id, request);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable Integer id) {
        try {
            adminCategoryService.toggleCategoryStatus(id);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        List<String> errorMessages = new java.util.ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMsg = error.getDefaultMessage();
            errorMessages.add(errorMsg);
        });

        String mainMessage = errorMessages.isEmpty() ? "Validation failed" : errorMessages.get(0);
        response.put("message", mainMessage);

        return ResponseEntity.badRequest().body(response);
    }
}

