package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.Orders;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.OrdersRepository;
import mocviet.repository.UserNotificationRepository;
import mocviet.service.manager.INotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final UserNotificationRepository notificationRepository;
    private final OrdersRepository ordersRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserNotification> getNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserNotification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    public String generateLinkFromNotification(UserNotification notification) {
        if (notification.getTitle() == null || notification.getMessage() == null) {
            return null;
        }

        String title = notification.getTitle();
        String message = notification.getMessage();

        if (title.contains("đánh giá") || title.contains("review") || title.contains("Review")) {
            Pattern reviewPattern = Pattern.compile("[Rr]eview #(\\d+)");
            Matcher reviewMatcher = reviewPattern.matcher(message);
            if (reviewMatcher.find()) {
                String reviewId = reviewMatcher.group(1);
                return "/manager/reviews/" + reviewId + "/respond";
            }
            return "/manager/reviews/alerts";
        }

        if (title.contains("đơn hàng") || title.contains("Order") ||
                title.contains("yêu cầu trả") || title.contains("phân công")) {
            Pattern orderPattern = Pattern.compile("[Đđ]ơn(?: hàng)? #(\\d+)");
            Matcher orderMatcher = orderPattern.matcher(message);
            if (orderMatcher.find()) {
                String orderId = orderMatcher.group(1);
                try {
                    Orders order = ordersRepository.findById(Integer.parseInt(orderId)).orElse(null);
                    if (order != null) {
                        Orders.OrderStatus status = order.getStatus();
                        Orders.ReturnStatus returnStatus = order.getReturnStatus();
                        if (returnStatus != null) {
                            switch (returnStatus) {
                                case REQUESTED:
                                    return "/manager/orders/returns/" + orderId;
                                case PROCESSED:
                                    return "/manager/orders/returned/" + orderId;
                                case APPROVED:
                                    return "/manager/orders/returns/" + orderId;
                                case REJECTED:
                                    return "/manager/orders/completed/" + orderId;
                            }
                        }
                        switch (status) {
                            case PENDING:
                            case CONFIRMED:
                                return "/manager/orders/pending/" + orderId;
                            case DISPATCHED:
                                return "/manager/orders/in-delivery/" + orderId;
                            case DELIVERED:
                                return "/manager/orders/completed/" + orderId;
                            case CANCELLED:
                                return "/manager/orders/cancelled/" + orderId;
                            case RETURNED:
                                return "/manager/orders/returned/" + orderId;
                        }
                    }
                } catch (Exception ignored) { }
                String lower = (title + " " + message).toLowerCase();
                if (lower.contains("chờ xác nhận") || lower.contains("pending") || lower.contains("đã xác nhận") || lower.contains("confirmed")) {
                    return "/manager/orders/pending/" + orderId;
                }
                if (lower.contains("đang giao") || lower.contains("in-delivery") || lower.contains("dispatched") || lower.contains("xuất kho")) {
                    return "/manager/orders/in-delivery/" + orderId;
                }
                if (lower.contains("đã hoàn thành") || lower.contains("completed") || lower.contains("delivered") || lower.contains("đã giao")) {
                    return "/manager/orders/completed/" + orderId;
                }
                if (lower.contains("yêu cầu hoàn trả") || lower.contains("returns") || lower.contains("trả hàng")) {
                    return "/manager/orders/returns/" + orderId;
                }
                if (lower.contains("đã hoàn trả") || lower.contains("returned") || lower.contains("hoàn trả thành công")) {
                    return "/manager/orders/returned/" + orderId;
                }
                if (lower.contains("đã hủy") || lower.contains("cancelled") || lower.contains("canceled")) {
                    return "/manager/orders/cancelled/" + orderId;
                }
                return "/manager/orders/pending/" + orderId;
            }
            return "/manager/orders";
        }

        if (title.contains("Tồn kho") || title.contains("Hết hàng") || title.contains("tồn kho")) {
            Pattern skuPattern = Pattern.compile("SKU ([A-Z0-9_]+)");
            Matcher skuMatcher = skuPattern.matcher(message);
            if (skuMatcher.find()) {
                return "/manager/inventory/low-stock";
            }
            return "/manager/inventory/alerts";
        }

        if (title.contains("Tin nhắn") || title.contains("tin nhắn")) {
            Pattern convPattern = Pattern.compile("cuộc hội thoại #(\\d+)");
            Matcher convMatcher = convPattern.matcher(message);
            if (convMatcher.find()) {
                String convId = convMatcher.group(1);
                return "/manager/messages/" + convId;
            }
        }

        return null;
    }
}


