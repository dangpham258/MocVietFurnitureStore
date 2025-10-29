package mocviet.service.manager;

import mocviet.service.manager.DashboardService.DashboardStats;
import mocviet.service.manager.DashboardService.NotificationDTO;
import mocviet.service.manager.DashboardService.RecentOrderDTO;

import java.util.List;

public interface IDashboardService {
    DashboardStats getDashboardStats();
    List<RecentOrderDTO> getRecentOrders(int limit);
    List<NotificationDTO> getNotifications();
}


