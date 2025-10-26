package mocviet.controller.admin.deliveryteams;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.*;
import mocviet.service.admin.AdminDeliveryTeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/delivery-teams/api")
@RequiredArgsConstructor
public class DeliveryTeamsApiController {
    
    private final AdminDeliveryTeamService adminDeliveryTeamService;
    
    @GetMapping("/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeliveryTeamResponse>> getAllTeams() {
        List<DeliveryTeamResponse> teams = adminDeliveryTeamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
    
    @GetMapping("/zones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ZoneInfoResponse>> getAllZones() {
        List<ZoneInfoResponse> zones = adminDeliveryTeamService.getAllZones();
        return ResponseEntity.ok(zones);
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AvailableUserResponse>> getAvailableUsers() {
        List<AvailableUserResponse> users = adminDeliveryTeamService.getAvailableUsers();
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTeam(@Valid @RequestBody DeliveryTeamCreateRequest request) {
        try {
            DeliveryTeamResponse team = adminDeliveryTeamService.createTeam(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", team);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTeam(@PathVariable Integer teamId, @Valid @RequestBody DeliveryTeamUpdateRequest request) {
        try {
            DeliveryTeamResponse team = adminDeliveryTeamService.updateTeam(teamId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", team);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/teams/{teamId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleTeamStatus(@PathVariable Integer teamId) {
        try {
            adminDeliveryTeamService.toggleTeamStatus(teamId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/teams/{teamId}/zones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addZoneToTeam(@PathVariable Integer teamId, @Valid @RequestBody ZoneMappingRequest request) {
        try {
            adminDeliveryTeamService.addZoneToTeam(teamId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/teams/zones/{mappingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeZoneFromTeam(@PathVariable Integer mappingId) {
        try {
            adminDeliveryTeamService.removeZoneFromTeam(mappingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
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

