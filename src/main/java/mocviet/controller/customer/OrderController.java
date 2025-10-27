package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.dto.OrderDetailDTO;
import mocviet.entity.Orders;
import mocviet.entity.OrderItem;
import mocviet.service.customer.IOrderService;
import mocviet.service.customer.impl.OrderServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final IOrderService orderService;
    private final OrderServiceImpl orderServiceImpl;
    
    /**
     * Trang quản lý đơn hàng
     */
    @GetMapping
    public String ordersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Orders> ordersPage;
        
        // Lọc theo trạng thái
        if (!"ALL".equals(status)) {
            try {
                Orders.OrderStatus orderStatus = Orders.OrderStatus.valueOf(status);
                List<Orders> orders = orderService.getCurrentUserOrdersByStatus(orderStatus);
                model.addAttribute("orders", orders);
                model.addAttribute("currentPage", 0);
                model.addAttribute("totalPages", 1);
                model.addAttribute("totalElements", orders.size());
            } catch (IllegalArgumentException e) {
                ordersPage = orderService.getCurrentUserOrders(pageable);
                model.addAttribute("orders", ordersPage.getContent());
                model.addAttribute("currentPage", ordersPage.getNumber());
                model.addAttribute("totalPages", ordersPage.getTotalPages());
                model.addAttribute("totalElements", ordersPage.getTotalElements());
            }
        } else {
            ordersPage = orderService.getCurrentUserOrders(pageable);
            model.addAttribute("orders", ordersPage.getContent());
            model.addAttribute("currentPage", ordersPage.getNumber());
            model.addAttribute("totalPages", ordersPage.getTotalPages());
            model.addAttribute("totalElements", ordersPage.getTotalElements());
        }
        
        // Thống kê đơn hàng
        Map<String, Long> statusCounts = orderService.getOrderCountsByStatus();
        
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("currentStatus", status);
        
        return "customer/orders";
    }
    
    /**
     * Trang chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, Model model) {
        OrderDetailDTO orderDetail = orderServiceImpl.getOrderDetailDTO(orderId);
        if (orderDetail == null) {
            return "redirect:/customer/orders?error=Không tìm thấy đơn hàng";
        }
        
        model.addAttribute("orderDetail", orderDetail);
        
        return "customer/order-detail";
    }
    
    /**
     * API hủy đơn hàng
     */
    @PostMapping("/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = orderService.cancelOrder(orderId, reason);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã hủy đơn hàng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể hủy đơn hàng này");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi hủy đơn hàng");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API yêu cầu trả hàng
     */
    @PostMapping("/{orderId}/return")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> requestReturn(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String reason) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = orderService.requestReturn(orderId, reason);
            if (success) {
                response.put("success", true);
                response.put("message", "Đã gửi yêu cầu trả hàng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể gửi yêu cầu trả hàng cho đơn hàng này");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi gửi yêu cầu trả hàng");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API mua lại đơn hàng
     */
    @PostMapping("/{orderId}/reorder")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reorderProducts(@PathVariable Integer orderId) {
        
        Map<String, Object> response = orderService.reorderProducts(orderId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * API lấy thống kê đơn hàng
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Long> statusCounts = orderService.getOrderCountsByStatus();
        Map<String, Long> returnStatusCounts = orderService.getOrderCountsByReturnStatus();
        
        response.put("statusCounts", statusCounts);
        response.put("returnStatusCounts", returnStatusCounts);
        
        return ResponseEntity.ok(response);
    }
}
