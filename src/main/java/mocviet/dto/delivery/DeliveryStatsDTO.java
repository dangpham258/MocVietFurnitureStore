package mocviet.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatsDTO {
    private long totalDoneToday;
    private long totalDoneThisWeek;
    private long totalDoneThisMonth;
}