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
public class ReportsResponseDTO {
    private StatsDTO stats;
    private List<RevenueDataDTO> revenueChart;
    private List<OrderStatusDataDTO> orderStatusChart;
    private List<CategoryRevenueDTO> categoryRevenue;
    private List<TopProductDTO> topProducts;
    private List<TopCustomerDTO> topCustomers;
    private List<RegionStatsDTO> regionStats;
}

