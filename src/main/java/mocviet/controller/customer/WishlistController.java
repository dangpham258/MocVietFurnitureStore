package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.entity.Wishlist;
import mocviet.repository.UserNotificationRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.IWishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    
    private final IWishlistService wishlistService;
    private final UserNotificationRepository notificationRepository;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Trang danh sách yêu thích
     */
    @GetMapping
    public String wishlistPage(Model model) {
        User currentUser = userDetailsService.getCurrentUser();
        List<Wishlist> wishlist = wishlistService.getCurrentUserWishlist();
        
        // Lấy notifications về sản phẩm có hàng trở lại
        List<UserNotification> backInStockNotifications = List.of();
        if (currentUser != null) {
            backInStockNotifications = notificationRepository
                .findByUserAndTitleAndIsReadFalseOrderByCreatedAtDesc(currentUser, "Sản phẩm có hàng trở lại")
                .stream()
                .limit(5) // Chỉ hiển thị 5 notification mới nhất
                .collect(Collectors.toList());
            
            // Đánh dấu các notification này là đã đọc (chỉ lần đầu tiên vào trang)
            if (!backInStockNotifications.isEmpty()) {
                backInStockNotifications.forEach(notif -> {
                    notif.setIsRead(true);
                    notificationRepository.save(notif);
                });
            }
        }
        
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("wishlistCount", wishlist.size());
        model.addAttribute("notifications", backInStockNotifications);
        
        return "customer/wishlist";
    }
    
    /**
     * API: Thêm sản phẩm vào wishlist
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToWishlist(@RequestParam Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Kiểm tra sản phẩm đã có trong wishlist chưa
            if (wishlistService.isInWishlist(productId)) {
                response.put("success", false);
                response.put("message", "Sản phẩm này đã có trong danh sách yêu thích của bạn");
            } else {
                boolean success = wishlistService.addToWishlist(productId);
                if (success) {
                    response.put("success", true);
                    response.put("message", "Đã thêm sản phẩm vào danh sách yêu thích");
                    response.put("wishlistCount", wishlistService.getWishlistCount());
                } else {
                    response.put("success", false);
                    response.put("message", "Không thể thêm sản phẩm vào danh sách yêu thích");
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Xóa sản phẩm khỏi wishlist
     */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@RequestParam Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = wishlistService.removeFromWishlist(productId);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã xóa sản phẩm khỏi danh sách yêu thích");
                response.put("wishlistCount", wishlistService.getWishlistCount());
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa sản phẩm khỏi danh sách yêu thích");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Xóa wishlist item theo ID
     */
    @PostMapping("/remove-item")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeWishlistItem(@RequestParam Integer wishlistId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = wishlistService.removeWishlistItem(wishlistId);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã xóa sản phẩm khỏi danh sách yêu thích");
                response.put("wishlistCount", wishlistService.getWishlistCount());
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa sản phẩm khỏi danh sách yêu thích");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Kiểm tra sản phẩm có trong wishlist không
     */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkInWishlist(@RequestParam Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean inWishlist = wishlistService.isInWishlist(productId);
            response.put("success", true);
            response.put("inWishlist", inWishlist);
        } catch (Exception e) {
            response.put("success", false);
            response.put("inWishlist", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Lấy số lượng wishlist
     */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWishlistCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = wishlistService.getWishlistCount();
            response.put("success", true);
            response.put("count", count);
        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API: Lấy số lượng notifications chưa đọc về sản phẩm có hàng trở lại
     */
    @GetMapping("/notifications/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBackInStockNotificationCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User currentUser = userDetailsService.getCurrentUser();
            if (currentUser != null) {
                Long count = notificationRepository.countByUserAndIsReadFalse(currentUser);
                response.put("success", true);
                response.put("count", count.intValue());
            } else {
                response.put("success", false);
                response.put("count", 0);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
        }
        
        return ResponseEntity.ok(response);
    }
}

