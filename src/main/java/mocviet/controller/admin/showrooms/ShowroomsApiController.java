package mocviet.controller.admin.showrooms;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ShowroomResponse;
import mocviet.dto.admin.ShowroomCreateRequest;
import mocviet.dto.admin.ShowroomUpdateRequest;
import mocviet.service.admin.AdminShowroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/showrooms/api")
@RequiredArgsConstructor
public class ShowroomsApiController {
    
    private final AdminShowroomService adminShowroomService;
    
    /**
     * Get all showrooms
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowroomResponse>> getAllShowrooms() {
        List<ShowroomResponse> showrooms = adminShowroomService.getAllShowrooms();
        return ResponseEntity.ok(showrooms);
    }
    
    /**
     * Get showroom by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowroomResponse> getShowroomById(@PathVariable Integer id) {
        ShowroomResponse showroom = adminShowroomService.getShowroomById(id);
        return ResponseEntity.ok(showroom);
    }
    
    /**
     * Create showroom
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShowroom(@RequestBody ShowroomCreateRequest request) {
        try {
            ShowroomResponse showroom = adminShowroomService.createShowroom(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", showroom);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Update showroom
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShowroom(
            @PathVariable Integer id,
            @RequestBody ShowroomUpdateRequest request) {
        try {
            ShowroomResponse showroom = adminShowroomService.updateShowroom(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", showroom);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Delete showroom
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShowroom(@PathVariable Integer id) {
        try {
            adminShowroomService.deleteShowroom(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa showroom thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Toggle showroom status
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleShowroomStatus(@PathVariable Integer id) {
        try {
            adminShowroomService.toggleShowroomStatus(id);
            
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

