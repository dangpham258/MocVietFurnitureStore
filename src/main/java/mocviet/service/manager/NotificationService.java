package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.UserNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final UserNotificationRepository notificationRepository;
    
    /**
     * Lấy danh sách thông báo với phân trang
     */
    @Transactional(readOnly = true)
    public Page<UserNotification> getNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    /**
     * Đếm số thông báo chưa đọc
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
    
    /**
     * Đánh dấu một thông báo là đã đọc
     */
    @Transactional
    public void markAsRead(Integer notificationId, User user) {
        // Kiểm tra thông báo có thuộc về user không
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        });
    }
    
    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUserId(user.getId());
    }
    
    /**
     * Lấy thông báo chưa đọc
     */
    @Transactional(readOnly = true)
    public java.util.List<UserNotification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    /**
     * Tạo link từ thông báo (dựa trên title và message)
     */
    public String generateLinkFromNotification(UserNotification notification) {
        if (notification.getTitle() == null || notification.getMessage() == null) {
            return null;
        }
        
        String title = notification.getTitle();
        String message = notification.getMessage();
        
        // Review notifications
        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review")) {
            // Try to extract review ID
            Pattern reviewPattern = Pattern.compile("[Rr]eview #(\\d+)");
            Matcher reviewMatcher = reviewPattern.matcher(message);
            if (reviewMatcher.find()) {
                String reviewId = reviewMatcher.group(1);
                return "/manager/reviews/" + reviewId + "/respond";
            }
            // Fallback to alerts page
            return "/manager/reviews/alerts";
        }
        
        // Order notifications
        if (title.contains("đơn hàng") || title.contains("Order") || 
            title.contains("yêu cầu trả") || title.contains("phân công")) {
            // Try to extract order ID
            Pattern orderPattern = Pattern.compile("[Đđ]ơn(?: hàng)? #(\\d+)");
            Matcher orderMatcher = orderPattern.matcher(message);
            if (orderMatcher.find()) {
                String orderId = orderMatcher.group(1);
                return "/manager/orders/" + orderId;
            }
            // Fallback to orders list
            return "/manager/orders";
        }
        
        // Inventory notifications
        if (title.contains("Tồn kho") || title.contains("Hết hàng") || title.contains("tồn kho")) {
            // Try to extract SKU
            Pattern skuPattern = Pattern.compile("SKU ([A-Z0-9_]+)");
            Matcher skuMatcher = skuPattern.matcher(message);
            if (skuMatcher.find()) {
                // For now, just go to inventory page
                return "/manager/inventory/low-stock";
            }
            return "/manager/inventory/alerts";
        }
        
        // Message/Chat notifications
        if (title.contains("Tin nhắn") || title.contains("tin nhắn")) {
            Pattern convPattern = Pattern.compile("cuộc hội thoại #(\\d+)");
            Matcher convMatcher = convPattern.matcher(message);
            if (convMatcher.find()) {
                String convId = convMatcher.group(1);
                return "/manager/messages/" + convId;
            }
        }
        
        return null; // No specific link
    }
}

