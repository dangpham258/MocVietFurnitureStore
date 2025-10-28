package mocviet.service.admin.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.admin.CategoryRevenueDTO;
import mocviet.dto.admin.OrderStatusDataDTO;
import mocviet.dto.admin.RegionStatsDTO;
import mocviet.dto.admin.ReportsResponseDTO;
import mocviet.dto.admin.RevenueDataDTO;
import mocviet.dto.admin.StatsDTO;
import mocviet.dto.admin.TopCustomerDTO;
import mocviet.dto.admin.TopProductDTO;
import mocviet.entity.Orders;
import mocviet.repository.OrdersRepository;
import mocviet.repository.ProductRepository;
import mocviet.service.admin.ReportsService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;

    @Override
    public ReportsResponseDTO getReports(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching reports from {} to {}", startDate, endDate);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        StatsDTO stats = calculateStats(start, end);
        List<RevenueDataDTO> revenueChart = calculateRevenueChart(start, end);
        List<OrderStatusDataDTO> orderStatusChart = calculateOrderStatusChart(start, end);
        List<CategoryRevenueDTO> categoryRevenue = calculateCategoryRevenue(start, end);
        List<TopProductDTO> topProducts = calculateTopProducts(start, end);
        List<TopCustomerDTO> topCustomers = calculateTopCustomers(start, end);
        List<RegionStatsDTO> regionStats = calculateRegionStats(start, end);

        return ReportsResponseDTO.builder()
                .stats(stats)
                .revenueChart(revenueChart)
                .orderStatusChart(orderStatusChart)
                .categoryRevenue(categoryRevenue)
                .topProducts(topProducts)
                .topCustomers(topCustomers)
                .regionStats(regionStats)
                .build();
    }

    private StatsDTO calculateStats(LocalDateTime start, LocalDateTime end) {
        long totalRevenue = ordersRepository.calculateRevenue(Orders.OrderStatus.DELIVERED, start, end);
        long totalOrders = ordersRepository.countOrdersInPeriod(start, end);
        long totalCustomers = ordersRepository.countDistinctCustomers(start, end);
        long totalProducts = productRepository.count();

        // Đếm theo trạng thái trong khoảng thời gian
        long pendingOrders = ordersRepository.countByStatusInPeriod(Orders.OrderStatus.PENDING, start, end);
        long confirmedOrders = ordersRepository.countByStatusInPeriod(Orders.OrderStatus.CONFIRMED, start, end);
        long dispatchedOrders = ordersRepository.countByStatusInPeriod(Orders.OrderStatus.DISPATCHED, start, end);
        long deliveredOrders = ordersRepository.countByStatusInPeriod(Orders.OrderStatus.DELIVERED, start, end);
        long cancelledOrders = ordersRepository.countByStatusInPeriod(Orders.OrderStatus.CANCELLED, start, end);

        return StatsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .dispatchedOrders(dispatchedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    private List<RevenueDataDTO> calculateRevenueChart(LocalDateTime start, LocalDateTime end) {
        List<Object[]> revenueData = ordersRepository.getRevenueByDate(Orders.OrderStatus.DELIVERED, start, end);

        List<RevenueDataDTO> chart = new ArrayList<>();
        for (Object[] row : revenueData) {
            String date = row[0].toString();
            Long revenue = ((Number) row[1]).longValue();
            chart.add(RevenueDataDTO.builder()
                    .date(date)
                    .revenue(revenue)
                    .build());
        }
        return chart;
    }

    private List<OrderStatusDataDTO> calculateOrderStatusChart(LocalDateTime start, LocalDateTime end) {
        long totalOrders = ordersRepository.countOrdersInPeriod(start, end);

        List<OrderStatusDataDTO> chart = new ArrayList<>();
        for (Orders.OrderStatus status : Orders.OrderStatus.values()) {
            long count = ordersRepository.countByStatusInPeriod(status, start, end);
            double percentage = totalOrders > 0 ? (count * 100.0 / totalOrders) : 0;

            chart.add(OrderStatusDataDTO.builder()
                    .status(status.name())
                    .count(count)
                    .percentage(percentage)
                    .build());
        }

        return chart;
    }

    private List<CategoryRevenueDTO> calculateCategoryRevenue(LocalDateTime start, LocalDateTime end) {
        List<Object[]> categoryData = ordersRepository.findRevenueByCategoryNative(
                "DELIVERED", start, end
        );

        List<CategoryRevenueDTO> result = new ArrayList<>();
        for (Object[] row : categoryData) {
            result.add(CategoryRevenueDTO.builder()
                    .categoryName(row[0].toString())
                    .revenue(((Number) row[1]).longValue())
                    .orderCount(((Number) row[2]).intValue())
                    .build());
        }
        return result;
    }

    private List<TopProductDTO> calculateTopProducts(LocalDateTime start, LocalDateTime end) {
        List<Object[]> productData = ordersRepository.findTopProductsByRevenue(Orders.OrderStatus.DELIVERED, start, end);

        List<TopProductDTO> result = new ArrayList<>();
        int rank = 1;
        for (Object[] row : productData) {
            result.add(TopProductDTO.builder()
                    .rank(rank++)
                    .productId(((Number) row[0]).intValue())
                    .productName(row[1].toString())
                    .totalSold(((Number) row[2]).longValue())
                    .revenue(((Number) row[3]).longValue())
                    .build());
            if (rank > 10) {
				break; // Top 10 only
			}
        }
        return result;
    }

    private List<TopCustomerDTO> calculateTopCustomers(LocalDateTime start, LocalDateTime end) {
        List<Object[]> customerData = ordersRepository.findTopCustomers(Orders.OrderStatus.DELIVERED, start, end);

        List<TopCustomerDTO> result = new ArrayList<>();
        int rank = 1;
        for (Object[] row : customerData) {
            result.add(TopCustomerDTO.builder()
                    .rank(rank++)
                    .customerId(((Number) row[0]).intValue())
                    .customerName(row[1] != null ? row[1].toString() : "Khách vãng lai")
                    .customerEmail(row[2] != null ? row[2].toString() : "")
                    .orderCount(((Number) row[3]).longValue())
                    .totalSpent(((Number) row[4]).longValue())
                    .build());
            if (rank > 10) {
				break; // Top 10 only
			}
        }
        return result;
    }

    private List<RegionStatsDTO> calculateRegionStats(LocalDateTime start, LocalDateTime end) {
        // Đơn giản hóa - group by city from Address, only DELIVERED orders
        List<Orders> orders = ordersRepository.findByCreatedAtBetween(start, end)
                .stream()
                .filter(o -> o.getStatus() == Orders.OrderStatus.DELIVERED)
                .toList();

        Map<String, RegionStatsDTO> regionMap = new HashMap<>();

        for (Orders order : orders) {
            String city = order.getAddress().getCity();

            RegionStatsDTO stats = regionMap.getOrDefault(city, RegionStatsDTO.builder()
                    .region(city)
                    .orderCount(0)
                    .totalRevenue(0)
                    .averageOrderValue(0)
                    .percentage(0)
                    .build());

            // Tính toán tổng đơn hàng
            long orderTotal = order.getOrderItems().stream()
                    .mapToLong(oi -> oi.getQty() * oi.getUnitPrice().longValue())
                    .sum();

            stats.setOrderCount(stats.getOrderCount() + 1);
            stats.setTotalRevenue(stats.getTotalRevenue() + orderTotal);

            regionMap.put(city, stats);
        }

        // Tính toán phần trăm và giá trị trung bình đơn hàng
        long totalRevenue = ordersRepository.calculateRevenue(Orders.OrderStatus.DELIVERED, start, end);

        List<RegionStatsDTO> result = new ArrayList<>(regionMap.values());
        for (RegionStatsDTO stats : result) {
            stats.setAverageOrderValue(stats.getOrderCount() > 0 ?
                    stats.getTotalRevenue() / stats.getOrderCount() : 0);
            stats.setPercentage(totalRevenue > 0 ?
                    (stats.getTotalRevenue() * 100.0 / totalRevenue) : 0);
        }

        result.sort((a, b) -> Long.compare(b.getTotalRevenue(), a.getTotalRevenue()));

        return result;
    }
}

