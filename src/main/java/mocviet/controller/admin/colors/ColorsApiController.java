package mocviet.controller.admin.colors;

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
import mocviet.dto.admin.ColorCreateRequest;
import mocviet.dto.admin.ColorResponse;
import mocviet.dto.admin.ColorUpdateRequest;
import mocviet.service.admin.AdminColorService;

@RestController
@RequestMapping("/admin/colors/api")
@RequiredArgsConstructor
public class ColorsApiController {

    private final AdminColorService adminColorService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ColorResponse>> getAllColors() {
        List<ColorResponse> colors = adminColorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorResponse> getColorById(@PathVariable Integer id) {
        ColorResponse color = adminColorService.getColorById(id);
        return ResponseEntity.ok(color);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ColorResponse>> searchColors(@RequestParam String keyword) {
        List<ColorResponse> colors = adminColorService.searchColors(keyword);
        return ResponseEntity.ok(colors);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createColor(@Valid @RequestBody ColorCreateRequest request) {
        try {
            ColorResponse color = adminColorService.createColor(request);
            return ResponseEntity.ok(color);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateColor(@PathVariable Integer id, @Valid @RequestBody ColorUpdateRequest request) {
        try {
            ColorResponse color = adminColorService.updateColor(id, request);
            return ResponseEntity.ok(color);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleColorStatus(@PathVariable Integer id) {
        try {
            adminColorService.toggleColorStatus(id);
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

