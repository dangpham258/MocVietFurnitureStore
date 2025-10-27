package mocviet.controller.delivery;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.MessageResponse;
import mocviet.dto.OrderDetailDTO;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.dto.delivery.DeliveryOrderDetailDTO;
import mocviet.dto.delivery.DeliveryOrderSummaryDTO;
import mocviet.dto.delivery.DeliveryStatsDTO;
import mocviet.dto.delivery.DeliveryUpdateRequestDTO;
import mocviet.entity.User;
import mocviet.repository.OrderRepository;
import mocviet.service.customer.impl.OrderServiceImpl;
import mocviet.service.delivery.IDeliveryService;

@Controller
@RequestMapping("/delivery")
@RequiredArgsConstructor // <<< Lombok sẽ tạo constructor cho các trường final
@PreAuthorize("hasRole('DELIVERY')")
public class DeliveryController {

    private final IDeliveryService deliveryService;
    // *** Đảm bảo có 'final' ở đây ***
    private final OrderRepository orderRepository;
    private final OrderServiceImpl orderServiceImpl;

    /** Trang chính (dashboard) - Hiển thị danh sách đơn cần giao */
    @GetMapping({"", "/"})
    public String dashboard(Authentication authentication, Model model) {
        try {
            List<DeliveryOrderSummaryDTO> assignedOrders = deliveryService.getAssignedOrders(authentication);
            model.addAttribute("orders", assignedOrders);
            model.addAttribute("pageTitle", "Đơn hàng cần xử lý");
            model.addAttribute("activeMenu", "dashboard");
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải danh sách đơn hàng: " + e.getMessage());
             model.addAttribute("activeMenu", "dashboard");
        }
        return "delivery/dashboard";
    }

    /** Trang chi tiết đơn hàng (Giao hàng) - sử dụng orderId */
    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, Authentication authentication, Model model) {
        try {
            DeliveryOrderDetailDTO orderDetail = deliveryService.getOrderDetailByOrderId(authentication, orderId);
            if (orderDetail == null) {
                throw new RuntimeException("Không tìm thấy thông tin chi tiết đơn hàng.");
            }
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + orderDetail.getOrderId());
            model.addAttribute("deliveryUpdate", new DeliveryUpdateRequestDTO());
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải chi tiết đơn hàng: " + e.getMessage());
             model.addAttribute("activeMenu", "dashboard");
             return "delivery/dashboard";
        }
        return "delivery/order_detail";
    }

    /** Trang chi tiết đơn hàng (Giao hàng) - sử dụng orderDeliveryId (giữ lại để tương thích) */
    @GetMapping("/orders/delivery/{orderDeliveryId}")
    public String orderDetailByDeliveryId(@PathVariable Integer orderDeliveryId, Authentication authentication, Model model) {
        try {
            DeliveryOrderDetailDTO orderDetail = deliveryService.getOrderDetail(authentication, orderDeliveryId);
            if (orderDetail == null) {
                throw new RuntimeException("Không tìm thấy thông tin chi tiết đơn hàng.");
            }
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + orderDetail.getOrderId());
            model.addAttribute("deliveryUpdate", new DeliveryUpdateRequestDTO());
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải chi tiết đơn hàng: " + e.getMessage());
             model.addAttribute("activeMenu", "dashboard");
             return "delivery/dashboard";
        }
        return "delivery/order_detail";
    }

    /** Xử lý xác nhận giao hàng - sử dụng orderId */
    @PostMapping("/orders/{orderId}/deliver")
    public String confirmDeliveryAction(
            @PathVariable Integer orderId,
            @ModelAttribute DeliveryUpdateRequestDTO request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        MessageResponse response = deliveryService.confirmDeliveryByOrderId(authentication, orderId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return "redirect:/delivery/orders/" + orderId;
        }
    }

    /** Xử lý xác nhận giao hàng - sử dụng orderDeliveryId (giữ lại để tương thích) */
    @PostMapping("/orders/delivery/{orderDeliveryId}/deliver")
    public String confirmDeliveryActionByDeliveryId(
            @PathVariable Integer orderDeliveryId,
            @ModelAttribute DeliveryUpdateRequestDTO request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        MessageResponse response = deliveryService.confirmDelivery(authentication, orderDeliveryId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return "redirect:/delivery/orders/delivery/" + orderDeliveryId;
        }
    }

    /** Xử lý thu hồi hàng - sử dụng orderId */
    @PostMapping("/orders/{orderId}/return")
    public String processReturnAction(
            @PathVariable Integer orderId,
            @ModelAttribute DeliveryUpdateRequestDTO request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

         if (request.getRefundMethod() == null || request.getRefundMethod().isBlank()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn phương thức hoàn tiền.");
             return "redirect:/delivery/orders/" + orderId;
         }

        MessageResponse response = deliveryService.processReturnPickupByOrderId(authentication, orderId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return "redirect:/delivery/orders/" + orderId;
        }
    }

    /** Xử lý thu hồi hàng - sử dụng orderDeliveryId (giữ lại để tương thích) */
    @PostMapping("/orders/delivery/{orderDeliveryId}/return")
    public String processReturnActionByDeliveryId(
            @PathVariable Integer orderDeliveryId,
            @ModelAttribute DeliveryUpdateRequestDTO request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

         if (request.getRefundMethod() == null || request.getRefundMethod().isBlank()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn phương thức hoàn tiền.");
             return "redirect:/delivery/orders/delivery/" + orderDeliveryId;
         }

        MessageResponse response = deliveryService.processReturnPickup(authentication, orderDeliveryId, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            return "redirect:/delivery";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
            return "redirect:/delivery/orders/delivery/" + orderDeliveryId;
        }
    }

    /** Trang lịch sử giao hàng */
    @GetMapping("/history")
    public String history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        try {
            Page<DeliveryOrderSummaryDTO> completedOrders = deliveryService.getCompletedOrders(authentication, pageable);
            DeliveryStatsDTO stats = deliveryService.getDeliveryStats(authentication);

            model.addAttribute("orderPage", completedOrders);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "Lịch sử giao hàng");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", completedOrders.getTotalPages());
            model.addAttribute("activeMenu", "history");
        } catch (RuntimeException e) {
             model.addAttribute("errorMessage", "Lỗi tải lịch sử: " + e.getMessage());
             model.addAttribute("stats", new DeliveryStatsDTO(0, 0, 0));
             model.addAttribute("activeMenu", "history");
        }
        return "delivery/history";
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

            model.addAttribute("user", currentUser);
            model.addAttribute("profileUpdateRequest", profileRequest);
            model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
            model.addAttribute("pageTitle", "Quản lý tài khoản");
            model.addAttribute("activeMenu", "profile");
         } catch (RuntimeException e) {
              model.addAttribute("errorMessage", "Lỗi tải thông tin tài khoản: " + e.getMessage());
               model.addAttribute("activeMenu", "profile");
         }
        return "delivery/profile";
    }

    /** Xử lý cập nhật profile */
    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute ProfileUpdateRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            try {
                User currentUser = deliveryService.getCurrentDeliveryProfile(authentication);
                model.addAttribute("user", currentUser);
                model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
                model.addAttribute("pageTitle", "Quản lý tài khoản");
                model.addAttribute("activeMenu", "profile");
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", "Lỗi tải thông tin tài khoản.");
                 model.addAttribute("activeMenu", "profile");
            }
             return "delivery/profile";
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
             Model model) {

        if (bindingResult.hasErrors()) {
             redirectAttributes.addFlashAttribute("passwordError", "Dữ liệu nhập không hợp lệ.");
             return "redirect:/delivery/profile";
        }

        MessageResponse response = deliveryService.changeDeliveryPassword(authentication, request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("passwordError", response.getMessage());
        }
        return "redirect:/delivery/profile";
    }

     /** Trang chi tiết đơn hàng gốc */
     @GetMapping("/orders/original/{orderId}")
     public String originalOrderDetail(@PathVariable Integer orderId, Authentication authentication, Model model) {
         try {
             // Kiểm tra đơn hàng có tồn tại không
             if (!orderRepository.existsById(orderId)) {
                  throw new RuntimeException("Không tìm thấy đơn hàng gốc.");
             }

             OrderDetailDTO orderDetailDTO = orderServiceImpl.getOrderDetailDTOForDelivery(orderId); // Sử dụng phương thức mới cho delivery team

             if (orderDetailDTO == null) {
                  // Lỗi này xảy ra trước đó, ném ra để đi vào catch block
                  throw new RuntimeException("Không thể lấy chi tiết đơn hàng gốc.");
             }

             model.addAttribute("orderDetail", orderDetailDTO);
             model.addAttribute("pageTitle", "Chi tiết đơn hàng gốc #" + orderId);

             return "delivery/original_order_detail"; // Trả về view mới

         } catch (RuntimeException e) {
              model.addAttribute("errorMessage", "Lỗi tải chi tiết đơn hàng gốc: " + e.getMessage());
              // *** Sửa lỗi chuyển hướng ở đây ***
              String errorMessage = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
              String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
              return "redirect:/delivery/history?error=" + encodedError; // Redirect về history với lỗi đã mã hóa
         }
     } // <<< Đảm bảo có dấu ngoặc này

} // <<< Dấu ngoặc đóng cuối cùng của class DeliveryController