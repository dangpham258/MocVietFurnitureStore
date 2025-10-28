package mocviet.controller.admin.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.DashboardStatsDTO;
import mocviet.service.admin.DashboardService;

@RestController
@RequestMapping("/admin/dashboard/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardApiController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}

