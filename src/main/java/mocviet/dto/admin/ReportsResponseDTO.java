package mocviet.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportsResponseDTO {
    private StatsDTO stats;
    private List<RevenueDataDTO> revenueChart;
    private List<OrderStatusDataDTO> orderStatusChart;
    private List<CategoryRevenueDTO> categoryRevenue;
    private List<TopProductDTO> topProducts;
    private List<TopCustomerDTO> topCustomers;
    private List<RegionStatsDTO> regionStats;
}

