package mocviet.controller.admin.banners;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.BannerResponse;
import mocviet.service.admin.AdminBannerService;

@RestController
@RequestMapping("/admin/banners/api")
@RequiredArgsConstructor
public class BannersApiController {

    private final AdminBannerService adminBannerService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BannerResponse>> getAllBanners() {
        List<BannerResponse> banners = adminBannerService.getAllBanners();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BannerResponse> getBannerById(@PathVariable Integer id) {
        BannerResponse banner = adminBannerService.getBannerById(id);
        return ResponseEntity.ok(banner);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBanner(
            @RequestParam("title") String title,
            @RequestParam(value = "linkUrl", required = false) String linkUrl,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam("image") MultipartFile image) {

        try {
            BannerResponse banner = adminBannerService.createBanner(title, linkUrl, isActive, orderNumber, image);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", banner);
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
    public ResponseEntity<?> updateBanner(
            @PathVariable Integer id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "linkUrl", required = false) String linkUrl,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            BannerResponse banner = adminBannerService.updateBanner(id, title, linkUrl, isActive, orderNumber, image);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", banner);
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
    public ResponseEntity<?> deleteBanner(@PathVariable Integer id) {
        try {
            adminBannerService.deleteBanner(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Banner deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

