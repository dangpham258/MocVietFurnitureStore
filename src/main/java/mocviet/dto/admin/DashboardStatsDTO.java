package mocviet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // Overview stats
    private long totalUsers;
    private long totalCategories;
    private long totalCoupons;
    private long revenueThisMonth;
    
    // Order counts
    private long ordersToday;
    private long ordersThisWeek;
    private long ordersThisMonth;
    
    // Revenue chart (7 days or current month by day)
    private List<RevenueDataDTO> revenueChart;
}

