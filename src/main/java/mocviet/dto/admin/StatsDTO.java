package mocviet.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private long totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private long pendingOrders;
    private long confirmedOrders;
    private long dispatchedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
}

