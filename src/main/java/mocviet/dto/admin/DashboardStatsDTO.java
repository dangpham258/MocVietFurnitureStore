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
public class DashboardStatsDTO {
    // Thống kê tổng quan
    private long totalUsers;
    private long totalCategories;
    private long totalCoupons;
    private long revenueThisMonth;

    // Số lượng đơn hàng
    private long ordersToday;
    private long ordersThisWeek;
    private long ordersThisMonth;

    // Biểu đồ doanh thu (7 ngày hoặc tháng hiện tại theo ngày)
    private List<RevenueDataDTO> revenueChart;
}

