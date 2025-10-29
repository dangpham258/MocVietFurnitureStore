package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Orders;
import mocviet.entity.ProductVariant;
import mocviet.entity.OrderDelivery;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.OrdersRepository;
import mocviet.repository.ProductRepository;
import mocviet.repository.ProductVariantRepository;
import mocviet.repository.ReviewRepository;
import mocviet.repository.OrderDeliveryRepository;
import mocviet.repository.UserNotificationRepository;
import mocviet.repository.UserRepository;
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
public class DashboardService {
    
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ReviewRepository reviewRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        // Đếm đơn hàng mới (trong 7 ngày qua)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        long newOrders = ordersRepository.countByCreatedAtAfter(sevenDaysAgo);
        stats.setNewOrders((int) newOrders);
        
        // Đếm tổng sản phẩm
        long totalProducts = productRepository.count();
        stats.setTotalProducts((int) totalProducts);
        
        // Đếm sản phẩm tồn kho thấp (1-5), không bao gồm hết hàng (0)
        List<ProductVariant> lowStockVariants = productVariantRepository.findByStockQtyBetweenAndIsActive(1, 5, true);
        stats.setLowStockProducts(lowStockVariants.size());
        
        // Đếm đánh giá mới (trong 7 ngày qua)
        long newReviews = reviewRepository.countByCreatedAtAfter(sevenDaysAgo);
        stats.setNewReviews((int) newReviews);
        
        // Đếm đơn hàng cần phân công
        long pendingAssignment = ordersRepository.countByStatusAndOrderDeliveryIsNull(Orders.OrderStatus.CONFIRMED);
        stats.setPendingAssignment((int) pendingAssignment);
        
        // Đếm đơn hàng đang giao
        long inDelivery = orderDeliveryRepository.countByStatus(OrderDelivery.DeliveryStatus.IN_TRANSIT);
        stats.setInDelivery((int) inDelivery);
        
        // Tính tổng doanh thu tháng này
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Orders> monthlyOrders = ordersRepository.findByCreatedAtAfterAndStatus(startOfMonth, Orders.OrderStatus.DELIVERED);
        BigDecimal monthlyRevenue = monthlyOrders.stream()
                .map(this::calculateOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthlyRevenue(monthlyRevenue);
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public List<RecentOrderDTO> getRecentOrders(int limit) {
        return ordersRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(this::mapToRecentOrderDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications() {
        // Lấy current user (manager)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return new java.util.ArrayList<>();
        }
        
        User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
        if (currentUser == null) {
            return new java.util.ArrayList<>();
        }
        
        // Lấy 10 thông báo mới nhất từ database (do trigger tự động tạo)
        List<UserNotification> dbNotifications = userNotificationRepository.findTop10ByUserOrderByCreatedAtDesc(currentUser);
        
        List<NotificationDTO> notifications = new java.util.ArrayList<>();
        
        // Convert UserNotification sang NotificationDTO
        for (UserNotification notif : dbNotifications) {
            String type = determineNotificationType(notif.getTitle());
            String link = determineLinkFromNotification(notif);
            
            notifications.add(new NotificationDTO(
                notif.getTitle(),
                notif.getMessage(),
                type,
                link,
                notif.getIsRead()
            ));
        }
        
        // Nếu không có thông báo từ DB, thêm thống kê thủ công (fallback)
        if (notifications.isEmpty()) {
            // Thông báo đánh giá chưa trả lời
            long unansweredReviews = reviewRepository.countByManagerResponseIsNull();
            if (unansweredReviews > 0) {
                notifications.add(new NotificationDTO(
                    "Có đánh giá mới",
                    "Có " + unansweredReviews + " đánh giá chưa trả lời",
                    "warning",
                    "/manager/reviews/alerts",
                    false
                ));
            }
            
            // Thông báo đơn hàng mới
            long newOrdersToday = ordersRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
            if (newOrdersToday > 0) {
                notifications.add(new NotificationDTO(
                    "Đơn hàng mới hôm nay",
                    "Có " + newOrdersToday + " đơn hàng mới cần xử lý",
                    "info",
                    "/manager/orders",
                    false
                ));
            }
            
            // Thông báo cần phân công
            long pendingAssignment = ordersRepository.countByStatusAndOrderDeliveryIsNull(Orders.OrderStatus.CONFIRMED);
            if (pendingAssignment > 0) {
                notifications.add(new NotificationDTO(
                    "Cần phân công đội giao hàng",
                    "Có " + pendingAssignment + " đơn hàng chờ phân công",
                    "warning",
                    "/manager/delivery/assign",
                    false
                ));
            }
            
            // Thông báo tồn kho thấp
            List<ProductVariant> criticalStockVariants = productVariantRepository.findByStockQtyLessThanAndIsActive(6, true);
            if (!criticalStockVariants.isEmpty()) {
                long outOfStock = criticalStockVariants.stream().filter(v -> v.getStockQty() == 0).count();
                long lowStock = criticalStockVariants.stream().filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5).count();
                
                String message;
                if (outOfStock > 0 && lowStock > 0) {
                    message = "Có " + outOfStock + " sản phẩm hết hàng và " + lowStock + " sản phẩm tồn kho thấp";
                } else if (outOfStock > 0) {
                    message = "Có " + outOfStock + " sản phẩm đã hết hàng, cần nhập kho gấp";
                } else {
                    message = "Có " + lowStock + " sản phẩm tồn kho thấp (1-5 sản phẩm)";
                }
                
                notifications.add(new NotificationDTO(
                    "⚠️ Cảnh báo tồn kho",
                    message,
                    "danger",
                    "/manager/inventory",
                    false
                ));
            }
        }
        
        return notifications;
    }
    
    /**
     * Xác định loại thông báo dựa trên title
     */
    private String determineNotificationType(String title) {
        if (title == null) return "info";
        
        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review")) {
            return "warning";
        } else if (title.contains("Hết hàng") || title.contains("Tồn kho thấp") || title.contains("yêu cầu trả")) {
            return "danger";
        } else if (title.contains("đơn hàng") || title.contains("Order")) {
            return "info";
        }
        return "info";
    }
    
    /**
     * Xác định link đến trang chi tiết từ nội dung thông báo
     */
    private String determineLinkFromNotification(UserNotification notification) {
        if (notification.getTitle() == null || notification.getMessage() == null) {
            return null;
        }
        
        String title = notification.getTitle();
        String message = notification.getMessage();
        
        // Review notifications
        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review") || 
            title.contains("phản hồi")) {
            // Extract review ID if present
            if (message.matches(".*Review #\\d+.*") || message.matches(".*review #\\d+.*")) {
                try {
                    String reviewIdStr = message.replaceAll(".*[Rr]eview #(\\d+).*", "$1");
                    return "/manager/reviews/" + reviewIdStr + "/respond";
                } catch (Exception e) {
                    // Fallback to alerts page
                }
            }
            return "/manager/reviews/alerts";
        }
        
        // Order notifications
        if (title.contains("đơn hàng") || title.contains("Order") || 
            title.contains("yêu cầu trả") || title.contains("phân công")) {
            // Extract order ID if present
            if (message.matches(".*[Đđ]ơn hàng #\\d+.*") || message.matches(".*[Đđ]ơn #\\d+.*")) {
                try {
                    String orderIdStr = message.replaceAll(".*[Đđ]ơn(?: hàng)? #(\\d+).*", "$1");
                    try {
                        Orders order = ordersRepository.findById(Integer.parseInt(orderIdStr)).orElse(null);
                        if (order != null) {
                            Orders.OrderStatus status = order.getStatus();
                            Orders.ReturnStatus returnStatus = order.getReturnStatus();
                            if (returnStatus != null) {
                                switch (returnStatus) {
                                    case REQUESTED:
                                        return "/manager/orders/returns/" + orderIdStr;
                                    case PROCESSED:
                                        return "/manager/orders/returned/" + orderIdStr;
                                    case APPROVED:
                                        return "/manager/orders/returns/" + orderIdStr;
                                    case REJECTED:
                                        return "/manager/orders/completed/" + orderIdStr;
                                }
                            }
                            switch (status) {
                                case PENDING:
                                case CONFIRMED:
                                    return "/manager/orders/pending/" + orderIdStr;
                                case DISPATCHED:
                                    return "/manager/orders/in-delivery/" + orderIdStr;
                                case DELIVERED:
                                    return "/manager/orders/completed/" + orderIdStr;
                                case CANCELLED:
                                    return "/manager/orders/cancelled/" + orderIdStr;
                                case RETURNED:
                                    return "/manager/orders/returned/" + orderIdStr;
                            }
                        }
                    } catch (Exception ignored) { }
                    // Fallback: suy luận theo text nếu không đọc được DB
                    String lower = (title + " " + message).toLowerCase();
                    if (lower.contains("chờ xác nhận") || lower.contains("pending") || lower.contains("đã xác nhận") || lower.contains("confirmed")) {
                        return "/manager/orders/pending/" + orderIdStr;
                    }
                    if (lower.contains("đang giao") || lower.contains("in-delivery") || lower.contains("dispatched") || lower.contains("xuất kho")) {
                        return "/manager/orders/in-delivery/" + orderIdStr;
                    }
                    if (lower.contains("đã hoàn thành") || lower.contains("completed") || lower.contains("delivered") || lower.contains("đã giao")) {
                        return "/manager/orders/completed/" + orderIdStr;
                    }
                    if (lower.contains("yêu cầu hoàn trả") || lower.contains("returns") || lower.contains("trả hàng")) {
                        return "/manager/orders/returns/" + orderIdStr;
                    }
                    if (lower.contains("đã hoàn trả") || lower.contains("returned") || lower.contains("hoàn trả thành công")) {
                        return "/manager/orders/returned/" + orderIdStr;
                    }
                    if (lower.contains("đã hủy") || lower.contains("cancelled") || lower.contains("canceled")) {
                        return "/manager/orders/cancelled/" + orderIdStr;
                    }
                    return "/manager/orders/pending/" + orderIdStr;
                } catch (Exception e) {
                    // Fallback to orders list
                }
            }
            return "/manager/orders";
        }
        
        // Inventory notifications
        if (title.contains("Tồn kho") || title.contains("Hết hàng") || title.contains("tồn kho")) {
            return "/manager/inventory";
        }
        
        return null; // No specific link
    }
    
    private BigDecimal calculateOrderTotal(Orders order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        }
        
        BigDecimal itemsTotal = order.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        return itemsTotal.add(shippingFee);
    }
    
    private RecentOrderDTO mapToRecentOrderDTO(Orders order) {
        RecentOrderDTO dto = new RecentOrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalAmount(calculateOrderTotal(order));
        return dto;
    }
    
    // DTO classes
    public static class DashboardStats {
        private int newOrders;
        private int totalProducts;
        private int lowStockProducts;
        private int newReviews;
        private int pendingAssignment;
        private int inDelivery;
        private BigDecimal monthlyRevenue;
        
        // Getters and setters
        public int getNewOrders() { return newOrders; }
        public void setNewOrders(int newOrders) { this.newOrders = newOrders; }
        
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
        
        public int getLowStockProducts() { return lowStockProducts; }
        public void setLowStockProducts(int lowStockProducts) { this.lowStockProducts = lowStockProducts; }
        
        public int getNewReviews() { return newReviews; }
        public void setNewReviews(int newReviews) { this.newReviews = newReviews; }
        
        public int getPendingAssignment() { return pendingAssignment; }
        public void setPendingAssignment(int pendingAssignment) { this.pendingAssignment = pendingAssignment; }
        
        public int getInDelivery() { return inDelivery; }
        public void setInDelivery(int inDelivery) { this.inDelivery = inDelivery; }
        
        public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
        public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    }
    
    public static class RecentOrderDTO {
        private Integer id;
        private String customerName;
        private String status;
        private LocalDateTime createdAt;
        private BigDecimal totalAmount;
        
        // Getters and setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }
    
    public static class NotificationDTO {
        private String title;
        private String message;
        private String type;
        private String link;       // Link đến trang chi tiết
        private Boolean isRead;     // Đã đọc chưa
        
        public NotificationDTO(String title, String message, String type) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.link = null;
            this.isRead = false;
        }
        
        public NotificationDTO(String title, String message, String type, String link, Boolean isRead) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.link = link;
            this.isRead = isRead;
        }
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        
        public Boolean getIsRead() { return isRead; }
        public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    }
}
