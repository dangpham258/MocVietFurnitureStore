package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.ReviewDTO;
import mocviet.dto.customer.ReviewRequestDTO;
import mocviet.dto.customer.UnreviewedItemDTO;
import mocviet.repository.OrderItemRepository;
import mocviet.service.customer.IReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final IReviewService reviewService;
    private final OrderItemRepository orderItemRepository;
    
    /**
     * Trang tạo đánh giá - hiển thị form đánh giá cho các sản phẩm trong đơn hàng
     */
    @GetMapping("/new")
    public String reviewForm(@RequestParam(required = false) Integer orderId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (orderId == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng");
            return "redirect:/customer/orders";
        }
        
        // Lấy danh sách sản phẩm chưa đánh giá trong đơn hàng
        List<UnreviewedItemDTO> unreviewedItems = reviewService.getUnreviewedItems(orderId);
        
        if (unreviewedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("success", "Tất cả sản phẩm trong đơn hàng đã được đánh giá");
            return "redirect:/customer/orders/" + orderId;
        }
        
        model.addAttribute("orderId", orderId);
        model.addAttribute("unreviewedItems", unreviewedItems);
        
        return "customer/review-form";
    }
    
    /**
     * API tạo đánh giá với multipart để upload ảnh
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @RequestParam("orderItemId") Integer orderItemId,
            @RequestParam("rating") Integer rating,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate dữ liệu
            if (rating == null || rating < 1 || rating > 5) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn điểm đánh giá từ 1-5 sao");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (content != null && content.length() > 1000) {
                response.put("success", false);
                response.put("message", "Nội dung đánh giá không vượt quá 1000 ký tự");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Kiểm tra order item có thể đánh giá không
            if (!reviewService.canReviewOrderItem(orderItemId)) {
                response.put("success", false);
                response.put("message", "Không thể đánh giá sản phẩm này");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate order item tồn tại
            var orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order item không tồn tại"));
            
            // Tạo request DTO
            ReviewRequestDTO request = new ReviewRequestDTO();
            request.setOrderItemId(orderItemId);
            request.setRating(rating);
            request.setContent(content);
            
            // Tạo review
            ReviewDTO review = reviewService.createReview(request);
            
            // Upload ảnh nếu có
            if (image != null && !image.isEmpty()) {
                String imageUrl = reviewService.uploadReviewImage(image, review.getId());
                // Update review với imageUrl
                reviewService.updateReviewImageUrl(review.getId(), imageUrl);
            }
            
            response.put("success", true);
            response.put("message", "Đánh giá đã được gửi thành công");
            response.put("reviewId", review.getId());
            response.put("orderId", orderItem.getOrder().getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Xem chi tiết đánh giá
     */
    @GetMapping("/{reviewId}")
    public String reviewDetail(@PathVariable Integer reviewId, Model model) {
        ReviewDTO review = reviewService.getReviewById(reviewId);
        if (review == null) {
            return "redirect:/customer/orders?error=Không tìm thấy đánh giá";
        }
        
        model.addAttribute("review", review);
        return "customer/review-detail";
    }
}

