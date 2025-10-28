package mocviet.controller.admin.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.StaticPageCreateRequest;
import mocviet.dto.admin.StaticPageResponse;
import mocviet.dto.admin.StaticPageUpdateRequest;
import mocviet.service.admin.AdminStaticPageService;

@RestController
@RequestMapping("/admin/pages/api")
@RequiredArgsConstructor
public class PagesApiController {

    private final AdminStaticPageService adminStaticPageService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StaticPageResponse>> getAllPages() {
        List<StaticPageResponse> pages = adminStaticPageService.getAllPages();
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaticPageResponse> getPageById(@PathVariable Integer id) {
        StaticPageResponse page = adminStaticPageService.getPageById(id);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPage(@Valid @RequestBody StaticPageCreateRequest request) {
        try {
            StaticPageResponse page = adminStaticPageService.createPage(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePage(
            @PathVariable Integer id,
            @Valid @RequestBody StaticPageUpdateRequest request) {
        try {
            StaticPageResponse page = adminStaticPageService.updatePage(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePage(@PathVariable Integer id) {
        try {
            adminStaticPageService.deletePage(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã xóa trang tĩnh thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> togglePageStatus(@PathVariable Integer id) {
        try {
            adminStaticPageService.togglePageStatus(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã cập nhật trạng thái thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

