package mocviet.controller.admin.sociallinks;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.SocialLinkCreateRequest;
import mocviet.dto.admin.SocialLinkResponse;
import mocviet.dto.admin.SocialLinkUpdateRequest;
import mocviet.service.admin.AdminSocialLinkService;

@RestController
@RequestMapping("/admin/social-links/api")
@RequiredArgsConstructor
public class SocialLinksApiController {

    private final AdminSocialLinkService adminSocialLinkService;

    @GetMapping("/links")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SocialLinkResponse>> getAllSocialLinks() {
        List<SocialLinkResponse> links = adminSocialLinkService.getAllSocialLinks();
        return ResponseEntity.ok(links);
    }

    @PutMapping("/links/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSocialLink(@PathVariable Integer id, @Valid @RequestBody SocialLinkUpdateRequest request) {
        try {
            SocialLinkResponse link = adminSocialLinkService.updateSocialLink(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", link);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/links")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSocialLink(@Valid @RequestBody SocialLinkCreateRequest request) {
        try {
            SocialLinkResponse link = adminSocialLinkService.createSocialLink(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", link);
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

