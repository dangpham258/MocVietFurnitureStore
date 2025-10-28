package mocviet.controller.admin.shipping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import mocviet.dto.admin.MappingResponse;
import mocviet.dto.admin.ProvinceMappingRequest;
import mocviet.dto.admin.ProvinceResponse;
import mocviet.dto.admin.ShippingFeeUpdateRequest;
import mocviet.dto.admin.ZoneResponse;
import mocviet.service.admin.AdminShippingService;

@RestController
@RequestMapping("/admin/shipping/api")
@RequiredArgsConstructor
public class ShippingApiController {

    private final AdminShippingService adminShippingService;

    @GetMapping("/zones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ZoneResponse>> getAllZones() {
        List<ZoneResponse> zones = adminShippingService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/provinces")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProvinceResponse>> getAllProvinces() {
        List<ProvinceResponse> provinces = adminShippingService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/mappings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MappingResponse>> getAllMappings() {
        List<MappingResponse> mappings = adminShippingService.getAllMappings();
        return ResponseEntity.ok(mappings);
    }

    @PutMapping("/fees/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShippingFee(@PathVariable Integer zoneId,
                                               @Valid @RequestBody ShippingFeeUpdateRequest request) {
        try {
            ZoneResponse zone = adminShippingService.updateShippingFee(zoneId, request);
            return ResponseEntity.ok(zone);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/mappings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProvinceMapping(@Valid @RequestBody ProvinceMappingRequest request) {
        try {
            MappingResponse mapping = adminShippingService.addProvinceMapping(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapping);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/mappings/{mappingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeProvinceMapping(@PathVariable Integer mappingId) {
        try {
            adminShippingService.removeProvinceMapping(mappingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa mapping thành công");
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

