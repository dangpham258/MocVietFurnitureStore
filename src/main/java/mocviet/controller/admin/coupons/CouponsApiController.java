package mocviet.controller.admin.coupons;

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
import mocviet.dto.admin.CouponCreateRequest;
import mocviet.dto.admin.CouponResponse;
import mocviet.dto.admin.CouponUpdateRequest;
import mocviet.service.admin.AdminCouponService;

@RestController
@RequestMapping("/admin/coupons/api")
@RequiredArgsConstructor
public class CouponsApiController {

    private final AdminCouponService adminCouponService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> coupons = adminCouponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String code) {
        CouponResponse coupon = adminCouponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        try {
            CouponResponse coupon = adminCouponService.createCoupon(request);
            return ResponseEntity.ok(coupon);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCoupon(@PathVariable String code,
                                          @Valid @RequestBody CouponUpdateRequest request) {
        try {
            CouponResponse coupon = adminCouponService.updateCoupon(code, request);
            return ResponseEntity.ok(coupon);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{code}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleCouponStatus(@PathVariable String code) {
        try {
            adminCouponService.toggleCouponStatus(code);
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

