package mocviet.controller.admin.showrooms;

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

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.ShowroomCreateRequest;
import mocviet.dto.admin.ShowroomResponse;
import mocviet.dto.admin.ShowroomUpdateRequest;
import mocviet.service.admin.AdminShowroomService;

@RestController
@RequestMapping("/admin/showrooms/api")
@RequiredArgsConstructor
public class ShowroomsApiController {

    private final AdminShowroomService adminShowroomService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowroomResponse>> getAllShowrooms() {
        List<ShowroomResponse> showrooms = adminShowroomService.getAllShowrooms();
        return ResponseEntity.ok(showrooms);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowroomResponse> getShowroomById(@PathVariable Integer id) {
        ShowroomResponse showroom = adminShowroomService.getShowroomById(id);
        return ResponseEntity.ok(showroom);
    }

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

