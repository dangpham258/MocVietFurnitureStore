package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryDTO {
    private Integer id;
    private String status;
    private String note;
    private LocalDateTime changedAt;
    private String changedByName; // <<<--- THÊM TRƯỜNG NÀY
}
