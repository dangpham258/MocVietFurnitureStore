package mocviet.controller.admin.reports;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.admin.ReportsResponseDTO;
import mocviet.service.admin.ReportsService;

@RestController
@RequestMapping("/admin/reports/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ReportsApiController {

    private final ReportsService reportsService;

    @GetMapping
    public ResponseEntity<ReportsResponseDTO> getReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Mặc định là tháng hiện tại nếu không được chỉ định
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        log.info("Fetching reports from {} to {}", startDate, endDate);

        ReportsResponseDTO reports = reportsService.getReports(startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}

