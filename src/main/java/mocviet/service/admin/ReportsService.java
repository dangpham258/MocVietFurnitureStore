package mocviet.service.admin;

import java.time.LocalDate;

import mocviet.dto.admin.ReportsResponseDTO;

public interface ReportsService {
    
    ReportsResponseDTO getReports(LocalDate startDate, LocalDate endDate);
}

