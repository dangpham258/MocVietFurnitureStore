package mocviet.service.admin;

import mocviet.dto.admin.ReportsResponseDTO;

import java.time.LocalDate;

public interface ReportsService {
    ReportsResponseDTO getReports(LocalDate startDate, LocalDate endDate);
}

