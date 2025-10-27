package mocviet.controller.delivery;

import jakarta.validation.Valid; // Thêm import
import lombok.RequiredArgsConstructor;
import mocviet.dto.MessageResponse;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.dto.delivery.DeliveryOrderDetailDTO;
import mocviet.dto.delivery.DeliveryOrderSummaryDTO;
import mocviet.dto.delivery.DeliveryUpdateRequestDTO;
import mocviet.dto.delivery.DeliveryStatsDTO; // Thêm import
import mocviet.entity.User;
import mocviet.service.delivery.IDeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import BindingResult
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/delivery") // Prefix chung cho Delivery
@RequiredArgsConstructor
@PreAuthorize("hasRole('DELIVERY')") // Yêu cầu role DELIVERY cho tất cả các method
public class DeliveryController {

    private final IDeliveryService deliveryService;

    /** Trang chính (dashboard) - Hiển thị danh sách đơn cần giao */
    @GetMapping({"", "/"})
    public String dashboard(Authentication authentication, Model model) {
        try {
            List<DeliveryOrderSummaryDTO> assignedOrders = deliveryService.getAssignedOrders(authentication);
            model.addAttribute("orders", assignedOrders);
            model.addAttribute("pageTitle", "Đơn hàng cần xử lý");
            model.addAttribute("activeMenu", "dashboard"); // <<<--- THÊM DÒNG NÀY
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải danh sách đơn hàng: " + e.getMessage());
             model.addAttribute("activeMenu", "dashboard"); // <<<--- THÊM KHI CÓ LỖI
        }
        return "delivery/dashboard"; // View: delivery/dashboard.html
    }

    /** Trang chi tiết đơn hàng */
    @GetMapping("/orders/{orderDeliveryId}")
    public String orderDetail(@PathVariable Integer orderDeliveryId, Authentication authentication, Model model) {
        try {
            DeliveryOrderDetailDTO orderDetail = deliveryService.getOrderDetail(authentication, orderDeliveryId);
             // Kiểm tra null trước khi truy cập orderDetail
            if (orderDetail == null) {
                throw new RuntimeException("Không tìm thấy thông tin chi tiết đơn hàng.");
            }
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + orderDetail.getOrderId());
            model.addAttribute("deliveryUpdate", new DeliveryUpdateRequestDTO());
            // Không cần activeMenu vì trang chi tiết không nằm trên menu chính
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải chi tiết đơn hàng: " + e.getMessage());
             model.addAttribute("activeMenu", "dashboard"); // <<<--- THÊM DÒNG NÀY (Để layout không lỗi nếu quay về dashboard)
             return "delivery/dashboard"; // Hoặc trang lỗi riêng
        }
        return "delivery/order_detail"; // View: delivery/order_detail.html
    }

    /** Xử lý xác nhận giao hàng */
    @PostMapping("/orders/{orderDeliveryId}/deliver")
    public String confirmDeliveryAction(
            @PathVariable Integer orderDeliveryId,
            @ModelAttribute DeliveryUpdateRequestDTO request, // Lấy dữ liệu từ form
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        MessageResponse response = deliveryService.confirmDelivery(authentication, orderDeliveryId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery"; // Quay về dashboard
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            // Quay lại trang chi tiết để hiển thị lỗi
            return "redirect:/delivery/orders/" + orderDeliveryId;
        }
    }
    
    

    /** Xử lý thu hồi hàng */
    @PostMapping("/orders/{orderDeliveryId}/return")
    public String processReturnAction(
            @PathVariable Integer orderDeliveryId,
            @ModelAttribute DeliveryUpdateRequestDTO request, // Lấy dữ liệu từ form
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Bắt buộc phải có refundMethod khi thu hồi
         if (request.getRefundMethod() == null || request.getRefundMethod().isBlank()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn phương thức hoàn tiền.");
             return "redirect:/delivery/orders/" + orderDeliveryId;
         }

        MessageResponse response = deliveryService.processReturnPickup(authentication, orderDeliveryId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery"; // Quay về dashboard
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            // Quay lại trang chi tiết để hiển thị lỗi
            return "redirect:/delivery/orders/" + orderDeliveryId;
        }
    }

    @GetMapping("/history")
    public String history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, 
            Authentication authentication,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        try {
            // Lấy danh sách phân trang (giữ nguyên)
            Page<DeliveryOrderSummaryDTO> completedOrders = deliveryService.getCompletedOrders(authentication, pageable);
            
            // <<<--- SỬA ĐỔI: Lấy thêm thống kê --- >>>
            DeliveryStatsDTO stats = deliveryService.getDeliveryStats(authentication);

            model.addAttribute("orderPage", completedOrders);
            model.addAttribute("stats", stats); // <<<--- THÊM DÒNG NÀY
            model.addAttribute("pageTitle", "Lịch sử giao hàng");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", completedOrders.getTotalPages());
            model.addAttribute("activeMenu", "history"); 
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải lịch sử: " + e.getMessage());
             model.addAttribute("stats", new DeliveryStatsDTO(0, 0, 0)); // <<<--- THÊM DÒNG NÀY (default)
             model.addAttribute("activeMenu", "history"); 
        }
        return "delivery/history"; // View: delivery/history.html
    }

    /** Trang quản lý tài khoản */
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
         try {
            User currentUser = deliveryService.getCurrentDeliveryProfile(authentication);
            ProfileUpdateRequest profileRequest = new ProfileUpdateRequest();
            profileRequest.setFullName(currentUser.getFullName());
            profileRequest.setEmail(currentUser.getEmail());
            profileRequest.setGender(currentUser.getGender());
            profileRequest.setDob(currentUser.getDob());
            profileRequest.setPhone(currentUser.getPhone());

            model.addAttribute("user", currentUser); // Hiển thị thông tin cơ bản
            model.addAttribute("profileUpdateRequest", profileRequest);
            model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
            model.addAttribute("pageTitle", "Quản lý tài khoản");
            model.addAttribute("activeMenu", "profile"); // <<<--- THÊM DÒNG NÀY
         } catch (RuntimeException e) {
              model.addAttribute("errorMessage", "Lỗi tải thông tin tài khoản: " + e.getMessage());
               model.addAttribute("activeMenu", "profile"); // <<<--- THÊM DÒNG NÀY
         }
        return "delivery/profile"; // View: delivery/profile.html
    }

    /** Xử lý cập nhật profile */
    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute ProfileUpdateRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) { // Thêm Model

         // Xử lý dobString nếu cần (tương tự ProfileController của Customer)
        // Ví dụ: Lấy dobString từ request param và parse
        /*
        String dobString = request.getParameter("dobString"); // Giả sử tên input là dobString
        if (dobString != null && !dobString.isEmpty()) {
            try {
                request.setDob(java.time.LocalDate.parse(dobString));
            } catch (Exception e) {
                bindingResult.rejectValue("dob", "dob.invalid", "Ngày sinh không hợp lệ");
            }
        }
        */

        if (bindingResult.hasErrors()) {
             // Nếu validation lỗi, cần load lại thông tin user để hiển thị trang profile
            try {
                User currentUser = deliveryService.getCurrentDeliveryProfile(authentication);
                model.addAttribute("user", currentUser);
                model.addAttribute("passwordChangeRequest", new PasswordChangeRequest()); // Cần DTO rỗng
                model.addAttribute("pageTitle", "Quản lý tài khoản");
                model.addAttribute("activeMenu", "profile"); // <<<--- THÊM KHI TRẢ VỀ VIEW
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", "Lỗi tải thông tin tài khoản.");
                 model.addAttribute("activeMenu", "profile"); // <<<--- THÊM KHI TRẢ VỀ VIEW
            }
             return "delivery/profile"; // Trả về view với lỗi validation
        }

        MessageResponse response = deliveryService.updateDeliveryProfile(authentication, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
        }
        return "redirect:/delivery/profile";
    }

    /** Xử lý đổi mật khẩu */
    @PostMapping("/profile/change-password")
    public String changePassword(
            @Valid @ModelAttribute PasswordChangeRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
             Model model) { // Thêm Model


        // Kiểm tra validation cơ bản (NotNull, Size nếu có)
        if (bindingResult.hasErrors()) {
             redirectAttributes.addFlashAttribute("passwordError", "Dữ liệu nhập không hợp lệ."); // Gửi lỗi chung
             return "redirect:/delivery/profile";
        }


        MessageResponse response = deliveryService.changeDeliveryPassword(authentication, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
        } else {
            // Gửi lỗi cụ thể về trang profile
            redirectAttributes.addFlashAttribute("passwordError", response.getMessage());
        }
        return "redirect:/delivery/profile";
    }
}