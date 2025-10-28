package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.admin.DashboardStatsDTO;
import mocviet.dto.admin.RevenueDataDTO;
import mocviet.entity.Orders;
import mocviet.repository.CategoryRepository;
import mocviet.repository.CouponRepository;
import mocviet.repository.OrdersRepository;
import mocviet.repository.UserRepository;
import mocviet.service.admin.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CouponRepository couponRepository;
    private final OrdersRepository ordersRepository;
    
    @Override
    public DashboardStatsDTO getDashboardStats() {
        log.info("Fetching dashboard statistics");
        
        // Overview stats
        long totalUsers = userRepository.count();
        long totalCategories = categoryRepository.count();
        long totalCoupons = couponRepository.count();
        
        // Calculate date ranges
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDateTime startOfWeek = weekStart.atStartOfDay();
        
        LocalDateTime startOfMonth = LocalDateTime.of(today.getYear(), today.getMonthValue(), 1, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(today.getYear(), today.getMonthValue(), 
                today.lengthOfMonth(), 23, 59);
        
        // Order counts
        long ordersToday = ordersRepository.findByCreatedAtBetween(startOfDay, endOfDay).size();
        long ordersThisWeek = ordersRepository.findByCreatedAtBetween(startOfWeek, endOfDay).size();
        long ordersThisMonth = ordersRepository.findByCreatedAtBetween(startOfMonth, endOfDay).size();
        
        // Revenue this month (only DELIVERED orders)
        long revenueThisMonth = ordersRepository.calculateRevenue(
                Orders.OrderStatus.DELIVERED, startOfMonth, endOfMonth);
        
        // Revenue chart - current month by day
        List<RevenueDataDTO> revenueChart = getRevenueChart(startOfMonth, endOfMonth);
        
        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalCategories(totalCategories)
                .totalCoupons(totalCoupons)
                .revenueThisMonth(revenueThisMonth)
                .ordersToday(ordersToday)
                .ordersThisWeek(ordersThisWeek)
                .ordersThisMonth(ordersThisMonth)
                .revenueChart(revenueChart)
                .build();
    }
    
    private List<RevenueDataDTO> getRevenueChart(LocalDateTime start, LocalDateTime end) {
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
}

