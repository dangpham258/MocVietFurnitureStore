package mocviet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionStatsDTO {
    private String region;
    private long orderCount;
    private long totalRevenue;
    private long averageOrderValue;
    private double percentage;
}

