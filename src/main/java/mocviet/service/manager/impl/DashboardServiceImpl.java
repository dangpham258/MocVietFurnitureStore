package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.OrderDelivery;
import mocviet.entity.Orders;
import mocviet.entity.ProductVariant;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.*;
import mocviet.service.manager.DashboardService;
import mocviet.service.manager.IDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewRepository reviewRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardService.DashboardStats getDashboardStats() {
        DashboardService.DashboardStats stats = new DashboardService.DashboardStats();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        long newOrders = ordersRepository.countByCreatedAtAfter(sevenDaysAgo);
        stats.setNewOrders((int) newOrders);
        long totalProducts = productRepository.count();
        stats.setTotalProducts((int) totalProducts);
        List<ProductVariant> lowStockVariants = productVariantRepository.findByStockQtyLessThanAndIsActive(6, true);
        stats.setLowStockProducts(lowStockVariants.size());
        long newReviews = reviewRepository.countByCreatedAtAfter(sevenDaysAgo);
        stats.setNewReviews((int) newReviews);
        long pendingAssignment = ordersRepository.countByStatusAndOrderDeliveryIsNull(Orders.OrderStatus.CONFIRMED);
        stats.setPendingAssignment((int) pendingAssignment);
        long inDelivery = orderDeliveryRepository.countByStatus(OrderDelivery.DeliveryStatus.IN_TRANSIT);
        stats.setInDelivery((int) inDelivery);
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Orders> monthlyOrders = ordersRepository.findByCreatedAtAfterAndStatus(startOfMonth, Orders.OrderStatus.DELIVERED);
        BigDecimal monthlyRevenue = monthlyOrders.stream().map(this::calculateOrderTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthlyRevenue(monthlyRevenue);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardService.RecentOrderDTO> getRecentOrders(int limit) {
        return ordersRepository.findTop10ByOrderByCreatedAtDesc().stream().limit(limit).map(this::mapToRecentOrderDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardService.NotificationDTO> getNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return new java.util.ArrayList<>();
        }
        User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
        if (currentUser == null) {
            return new java.util.ArrayList<>();
        }
        List<UserNotification> dbNotifications = userNotificationRepository.findTop10ByUserOrderByCreatedAtDesc(currentUser);
        List<DashboardService.NotificationDTO> notifications = new java.util.ArrayList<>();
        for (UserNotification notif : dbNotifications) {
            String type = determineNotificationType(notif.getTitle());
            String link = determineLinkFromNotification(notif);
            notifications.add(new DashboardService.NotificationDTO(notif.getTitle(), notif.getMessage(), type, link, notif.getIsRead()));
        }
        if (notifications.isEmpty()) {
            long unansweredReviews = reviewRepository.countByManagerResponseIsNull();
            if (unansweredReviews > 0) {
                notifications.add(new DashboardService.NotificationDTO("Có đánh giá mới", "Có " + unansweredReviews + " đánh giá chưa trả lời", "warning", "/manager/reviews/alerts", false));
            }
            long newOrdersToday = ordersRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            if (newOrdersToday > 0) {
                notifications.add(new DashboardService.NotificationDTO("Đơn hàng mới hôm nay", "Có " + newOrdersToday + " đơn hàng mới cần xử lý", "info", "/manager/orders", false));
            }
            long pendingAssignment = ordersRepository.countByStatusAndOrderDeliveryIsNull(Orders.OrderStatus.CONFIRMED);
            if (pendingAssignment > 0) {
                notifications.add(new DashboardService.NotificationDTO("Cần phân công đội giao hàng", "Có " + pendingAssignment + " đơn hàng chờ phân công", "warning", "/manager/delivery/assign", false));
            }
            List<ProductVariant> criticalStockVariants = productVariantRepository.findByStockQtyLessThanAndIsActive(6, true);
            if (!criticalStockVariants.isEmpty()) {
                long outOfStock = criticalStockVariants.stream().filter(v -> v.getStockQty() == 0).count();
                long lowStock = criticalStockVariants.stream().filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5).count();
                String message = (outOfStock > 0 && lowStock > 0) ? ("Có " + outOfStock + " sản phẩm hết hàng và " + lowStock + " sản phẩm tồn kho thấp") : (outOfStock > 0 ? ("Có " + outOfStock + " sản phẩm đã hết hàng, cần nhập kho gấp") : ("Có " + lowStock + " sản phẩm tồn kho thấp (1-5 sản phẩm)"));
                notifications.add(new DashboardService.NotificationDTO("⚠️ Cảnh báo tồn kho", message, "danger", "/manager/inventory", false));
            }
        }
        return notifications;
    }

    private String determineNotificationType(String title) {
        if (title == null) return "info";
        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review")) return "warning";
        else if (title.contains("Hết hàng") || title.contains("Tồn kho thấp") || title.contains("yêu cầu trả")) return "danger";
        else if (title.contains("đơn hàng") || title.contains("Order")) return "info";
        return "info";
    }

    private String determineLinkFromNotification(UserNotification notification) {
        if (notification.getTitle() == null || notification.getMessage() == null) return null;
        String title = notification.getTitle();
        String message = notification.getMessage();
        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review") || title.contains("phản hồi")) {
            if (message.matches(".*Review #\\d+.*") || message.matches(".*review #\\d+.*")) {
                try {
                    String reviewIdStr = message.replaceAll(".*[Rr]eview #(\\d+).*", "$1");
                    return "/manager/reviews/" + reviewIdStr + "/respond";
                } catch (Exception ignored) {}
            }
            return "/manager/reviews/alerts";
        }
        if (title.contains("đơn hàng") || title.contains("Order") || title.contains("yêu cầu trả") || title.contains("phân công")) {
            if (message.matches(".*[Đđ]ơn hàng #\\d+.*") || message.matches(".*[Đđ]ơn #\\d+.*")) {
                try {
                    String orderIdStr = message.replaceAll(".*[Đđ]ơn(?: hàng)? #(\\d+).*$", "$1");
                    Orders order = ordersRepository.findById(Integer.parseInt(orderIdStr)).orElse(null);
                    if (order != null) {
                        Orders.OrderStatus status = order.getStatus();
                        Orders.ReturnStatus returnStatus = order.getReturnStatus();
                        if (returnStatus != null) {
                            switch (returnStatus) {
                                case REQUESTED: return "/manager/orders/returns/" + orderIdStr;
                                case PROCESSED: return "/manager/orders/returned/" + orderIdStr;
                                case APPROVED: return "/manager/orders/returns/" + orderIdStr;
                                case REJECTED: return "/manager/orders/completed/" + orderIdStr;
                            }
                        }
                        switch (status) {
                            case PENDING:
                            case CONFIRMED: return "/manager/orders/pending/" + orderIdStr;
                            case DISPATCHED: return "/manager/orders/in-delivery/" + orderIdStr;
                            case DELIVERED: return "/manager/orders/completed/" + orderIdStr;
                            case CANCELLED: return "/manager/orders/cancelled/" + orderIdStr;
                            case RETURNED: return "/manager/orders/returned/" + orderIdStr;
                        }
                    }
                } catch (Exception ignored) {}
                String lower = (title + " " + message).toLowerCase();
                if (lower.contains("chờ xác nhận") || lower.contains("pending") || lower.contains("đã xác nhận") || lower.contains("confirmed")) return "/manager/orders/pending/" + message;
                if (lower.contains("đang giao") || lower.contains("in-delivery") || lower.contains("dispatched") || lower.contains("xuất kho")) return "/manager/orders/in-delivery/" + message;
                if (lower.contains("đã hoàn thành") || lower.contains("completed") || lower.contains("delivered") || lower.contains("đã giao")) return "/manager/orders/completed/" + message;
                if (lower.contains("yêu cầu hoàn trả") || lower.contains("returns") || lower.contains("trả hàng")) return "/manager/orders/returns/" + message;
                if (lower.contains("đã hoàn trả") || lower.contains("returned") || lower.contains("hoàn trả thành công")) return "/manager/orders/returned/" + message;
                if (lower.contains("đã hủy") || lower.contains("cancelled") || lower.contains("canceled")) return "/manager/orders/cancelled/" + message;
                return "/manager/orders/pending/" + message;
            }
            return "/manager/orders";
        }
        if (title.contains("Tồn kho") || title.contains("Hết hàng") || title.contains("tồn kho")) {
            return "/manager/inventory";
        }
        return null;
    }

    private BigDecimal calculateOrderTotal(Orders order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        }
        BigDecimal itemsTotal = order.getOrderItems().stream().map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        return itemsTotal.add(shippingFee);
    }

    private DashboardService.RecentOrderDTO mapToRecentOrderDTO(Orders order) {
        DashboardService.RecentOrderDTO dto = new DashboardService.RecentOrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalAmount(calculateOrderTotal(order));
        return dto;
    }
}


