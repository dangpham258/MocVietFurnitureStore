package mocviet.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Product;
import mocviet.entity.User;
import mocviet.repository.ProductRepository;
import mocviet.service.manager.IReviewManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller quản lý đánh giá/bình luận cho Manager
 * Route: /manager/reviews/*
 */
@Controller
@RequestMapping("/manager/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ReviewManagementController {
    
    private final IReviewManagementService reviewService;
    private final ProductRepository productRepository;
    
    // ===== TRANG CHỦ QUẢN LÝ ĐÁNH GIÁ =====
    
    @GetMapping("")
    public String reviewsHome() {
        return "redirect:/manager/reviews/all";
    }
    
    // ===== UC-MGR-REV-ViewAllReviews: XEM TẤT CẢ ĐÁNH GIÁ =====
    
    @GetMapping("/all")
    public String viewAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean isHidden,
            @RequestParam(required = false) Boolean unanswered,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "reviews");
        model.addAttribute("activeSubMenu", "all-reviews");
        model.addAttribute("pageTitle", "Quản lý đánh giá");
        
        // Get review stats for dashboard
        ReviewStatsDTO stats = reviewService.getReviewStats();
        model.addAttribute("stats", stats);
        
        // Create filter DTO
        ReviewFilterDTO filter = new ReviewFilterDTO();
        filter.setKeyword(keyword);
        filter.setProductId(productId);
        filter.setRating(rating);
        filter.setIsHidden(isHidden);
        filter.setUnanswered(unanswered);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);
        
        // Create pageable
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Get reviews
        Page<ReviewDTO> reviews = reviewService.getReviews(filter, pageable);
        
        // Add to model
        model.addAttribute("reviews", reviews);
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviews.getTotalPages());
        model.addAttribute("totalItems", reviews.getTotalElements());
        
        // Get all products for filter dropdown
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        
        return "manager/reviews/all-reviews";
    }
    
    /**
     * Xem chi tiết đánh giá (Modal hoặc trang riêng)
     */
    @GetMapping("/{id}")
    public String viewReviewDetail(@PathVariable Integer id, Model model) {
        ReviewDTO review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        model.addAttribute("activeMenu", "reviews");
        model.addAttribute("pageTitle", "Chi tiết đánh giá");
        return "manager/reviews/review-detail";
    }
    
    // ===== UC-MGR-REV-RespondToReview: TRẢ LỜI ĐÁNH GIÁ =====
    
    /**
     * Form trả lời đánh giá
     */
    @GetMapping("/{id}/respond")
    public String respondForm(@PathVariable Integer id, Model model) {
        ReviewDTO review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        model.addAttribute("responseRequest", new ReviewResponseRequest());
        model.addAttribute("activeMenu", "reviews");
        model.addAttribute("pageTitle", "Trả lời đánh giá");
        return "manager/reviews/respond-form";
    }
    
    /**
     * Xử lý trả lời đánh giá
     */
    @PostMapping("/{id}/respond")
    public String respondToReview(
            @PathVariable Integer id,
            @Valid @ModelAttribute("responseRequest") ReviewResponseRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            ReviewDTO review = reviewService.getReviewById(id);
            model.addAttribute("review", review);
            model.addAttribute("activeMenu", "reviews");
            return "manager/reviews/respond-form";
        }
        
        try {
            User manager = (User) authentication.getPrincipal();
            reviewService.respondToReview(id, request.getResponse(), manager);
            redirectAttributes.addFlashAttribute("successMessage", "Trả lời đánh giá thành công!");
            return "redirect:/manager/reviews/all";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/manager/reviews/" + id + "/respond";
        }
    }
    
    /**
     * Sửa phản hồi đã gửi
     */
    @PostMapping("/{id}/update-response")
    public String updateResponse(
            @PathVariable Integer id,
            @RequestParam String response,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            reviewService.updateResponse(id, response, manager);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phản hồi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/manager/reviews/all";
    }
    
    /**
     * Xóa phản hồi
     */
    @PostMapping("/{id}/delete-response")
    public String deleteResponse(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            reviewService.deleteResponse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa phản hồi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/manager/reviews/all";
    }
    
    // ===== UC-MGR-REV-HideShowReview: ẨN/HIỆN ĐÁNH GIÁ =====
    
    /**
     * Toggle ẩn/hiện đánh giá
     */
    @PostMapping("/{id}/toggle-visibility")
    public String toggleVisibility(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            reviewService.toggleReviewVisibility(id);
            redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/manager/reviews/all";
    }
    
    /**
     * Ẩn/hiện hàng loạt
     */
    @PostMapping("/bulk-toggle")
    public String bulkToggle(
            @RequestParam List<Integer> reviewIds,
            @RequestParam Boolean hide,
            RedirectAttributes redirectAttributes) {
        
        try {
            reviewService.bulkToggleVisibility(reviewIds, hide);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã " + (hide ? "ẩn" : "hiện") + " " + reviewIds.size() + " đánh giá!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/manager/reviews/all";
    }
    
    // ===== UC-MGR-REV-ManageReviewAlerts: QUẢN LÝ CẢNH BÁO ĐÁNH GIÁ MỚI =====
    
    @GetMapping("/alerts")
    public String reviewAlerts(Model model) {
        model.addAttribute("activeMenu", "reviews");
        model.addAttribute("activeSubMenu", "review-alerts");
        model.addAttribute("pageTitle", "Cảnh báo đánh giá");
        
        // Get review stats
        ReviewStatsDTO stats = reviewService.getReviewStats();
        model.addAttribute("stats", stats);
        
        // Get alerts
        List<ReviewAlertDTO> alerts = reviewService.getReviewAlerts();
        model.addAttribute("alerts", alerts);
        
        return "manager/reviews/alerts";
    }
}

