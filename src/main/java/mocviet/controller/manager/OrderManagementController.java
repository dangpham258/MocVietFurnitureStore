package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.User;
import mocviet.service.manager.IOrderManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/manager/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class OrderManagementController {
    
    private final IOrderManagementService orderManagementService;
    
    // ===== TRANG CHỦ QUẢN LÝ ĐƠN HÀNG =====
    
    @GetMapping("")
    public String ordersHome() {
        return "redirect:/manager/orders/pending";
    }
    
    // ===== XÁC NHẬN ĐƠN HÀNG =====
    
    @GetMapping("/pending")
    public String pendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "pending");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field (tương tự DeliveryAssignment)
        String validSortBy = sortBy;
        if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && 
            !"id".equals(sortBy) && !"totalAmount".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderListDTO> orders = orderManagementService.getPendingOrders(pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Đơn hàng chờ xác nhận");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/pending";
    }
    
    @GetMapping("/pending/{id}")
    public String pendingOrderDetail(@PathVariable Integer id, Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "pending");
        try {
            OrderManagementDTO order = orderManagementService.getOrderDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
            return "manager/orders/pending-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/pending";
        }
    }
    
    @PostMapping("/pending/{id}/confirm")
    public String confirmOrder(
            @PathVariable Integer id,
            @RequestParam(required = false) String note,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            orderManagementService.confirmOrder(id, manager.getId(), note);
            redirectAttributes.addFlashAttribute("success", "Xác nhận đơn hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/orders/pending";
    }
    
    @PostMapping("/pending/{id}/cancel")
    public String cancelOrder(
            @PathVariable Integer id,
            @RequestParam String reason,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            orderManagementService.cancelOrder(id, manager.getId(), reason);
            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/orders/pending";
    }
    
    // ===== XEM ĐƠN HÀNG ĐANG GIAO =====
    
    @GetMapping("/in-delivery")
    public String inDeliveryOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "in-delivery");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field
        String validSortBy = sortBy;
        if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && 
            !"id".equals(sortBy) && !"totalAmount".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderListDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderManagementService.getInDeliveryOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            orders = orderManagementService.getInDeliveryOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Đơn hàng đang giao");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/in-delivery";
    }
    
    @GetMapping("/in-delivery/{id}")
    public String inDeliveryOrderDetail(@PathVariable Integer id, Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "in-delivery");
        
        try {
            OrderManagementDTO order = orderManagementService.getOrderDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
            return "manager/orders/in-delivery-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/in-delivery";
        }
    }
    
    // ===== XEM ĐƠN HÀNG ĐÃ HỦY =====
    
    @GetMapping("/cancelled")
    public String cancelledOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "cancelled");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field
        String validSortBy = sortBy;
        if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && 
            !"id".equals(sortBy) && !"totalAmount".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderListDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderManagementService.getCancelledOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            orders = orderManagementService.getCancelledOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Đơn hàng đã hủy");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/cancelled";
    }
    
    @GetMapping("/cancelled/{id}")
    public String cancelledOrderDetail(@PathVariable Integer id, Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "cancelled");
        
        try {
            OrderManagementDTO order = orderManagementService.getOrderDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
            return "manager/orders/cancelled-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/cancelled";
        }
    }
    
    // ===== XEM ĐƠN HÀNG HOÀN THÀNH =====
    
    @GetMapping("/completed")
    public String completedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "completed");
        
        // Reset page về 0 khi có filter mới
        if ((keyword != null && !keyword.trim().isEmpty()) || 
            (fromDate != null && !fromDate.trim().isEmpty()) || 
            (toDate != null && !toDate.trim().isEmpty())) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field (tương tự DeliveryAssignment)
        String validSortBy = sortBy;
        if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && 
            !"id".equals(sortBy) && !"totalAmount".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Parse dates
        LocalDateTime from = null;
        LocalDateTime to = null;
        
        try {
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                from = LocalDateTime.parse(fromDate + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                to = LocalDateTime.parse(toDate + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            // Invalid date format, ignore filters
        }
        
        Page<OrderListDTO> orders = orderManagementService.getCompletedOrdersWithFilters(
            keyword, from, to, pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("pageTitle", "Đơn hàng hoàn thành");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/completed";
    }
    
    @GetMapping("/completed/{id}")
    public String completedOrderDetail(@PathVariable Integer id, Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "completed");
        
        try {
            OrderManagementDTO order = orderManagementService.getOrderDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
            return "manager/orders/completed-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/completed";
        }
    }
    
    @GetMapping("/completed/export")
    public void exportCompletedOrdersToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        
        // Parse dates
        LocalDateTime from = null;
        LocalDateTime to = null;
        
        try {
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                from = LocalDateTime.parse(fromDate + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                to = LocalDateTime.parse(toDate + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            // Invalid date format
        }
        
        // Get all orders without pagination for export
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        
        Page<OrderListDTO> orders = orderManagementService.getCompletedOrdersWithFilters(
            keyword, from, to, pageable);
        
        // Set response headers for Excel download
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", 
            "attachment; filename=don-hang-hoan-thanh-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xls");
        
        // Write Excel content
        java.io.PrintWriter writer = response.getWriter();
        
        // Header row
        writer.println("Mã đơn\tKhách hàng\tSố điện thoại\tĐịa chỉ\tTỉnh/TP\t" +
                      "Trạng thái\tThanh toán\tTổng tiền\tSố SP\tĐội giao\tNgày tạo");
        
        // Data rows
        for (OrderListDTO order : orders.getContent()) {
            writer.println(
                order.getId() + "\t" +
                order.getCustomerName() + "\t" +
                order.getCustomerPhone() + "\t" +
                order.getDeliveryAddress() + "\t" +
                order.getCity() + "\t" +
                order.getStatusDisplay() + "\t" +
                order.getPaymentStatusDisplay() + "\t" +
                order.getTotalAmount() + "\t" +
                order.getItemCount() + "\t" +
                (order.getDeliveryTeamName() != null ? order.getDeliveryTeamName() : "") + "\t" +
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        }
        
        writer.flush();
    }
    
    // ===== XEM ĐƠN HÀNG ĐÃ HOÀN TRẢ =====
    
    @GetMapping("/returned")
    public String returnedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "returned");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field
        String validSortBy = sortBy;
        if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && 
            !"id".equals(sortBy) && !"totalAmount".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderListDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderManagementService.getReturnedOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            orders = orderManagementService.getReturnedOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("totalElements", orders.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Đơn hàng đã hoàn trả");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/returned";
    }
    
    @GetMapping("/returned/{id}")
    public String returnedOrderDetail(@PathVariable Integer id, Model model) {
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "returned");
        try {
            OrderManagementDTO order = orderManagementService.getOrderDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
            return "manager/orders/returned-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/returned";
        }
    }
    
    @GetMapping("/returned/export")
    public void exportReturnedOrdersToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        
        // Get all orders without pagination for export
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        
        Page<OrderListDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = orderManagementService.getReturnedOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            orders = orderManagementService.getReturnedOrders(pageable);
        }
        
        // Set response headers for Excel download
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", 
            "attachment; filename=don-hang-da-hoan-tra-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xls");
        
        // Write Excel content
        java.io.PrintWriter writer = response.getWriter();
        
        // Header row
        writer.println("Mã đơn\tKhách hàng\tSố điện thoại\tĐịa chỉ\tTỉnh/TP\t" +
                      "Trạng thái\tTrạng thái trả\tTổng tiền\tSố SP\tĐội giao\tNgày tạo\tNgày cập nhật");
        
        // Data rows
        for (OrderListDTO order : orders.getContent()) {
            writer.println(
                order.getId() + "\t" +
                order.getCustomerName() + "\t" +
                order.getCustomerPhone() + "\t" +
                order.getDeliveryAddress() + "\t" +
                order.getCity() + "\t" +
                "ĐÃ TRẢ HÀNG\t" +
                "ĐÃ XỬ LÝ\t" +
                order.getTotalAmount() + "\t" +
                order.getItemCount() + "\t" +
                (order.getDeliveryTeamName() != null ? order.getDeliveryTeamName() : "") + "\t" +
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\t" +
                order.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        }
        
        writer.flush();
    }
    
    // ===== QUẢN LÝ YÊU CẦU TRẢ HÀNG =====
    
    @GetMapping("/returns")
    public String returnRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "returns");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate sortBy field - chỉ cho phép các field tồn tại trong Entity Orders
        String validSortBy = sortBy;
        if ("returnRequestedAt".equals(sortBy) || "orderDeliveredAt".equals(sortBy) || "totalAmount".equals(sortBy)) {
            validSortBy = "updatedAt"; // fallback về updatedAt cho các field không có trong entity
        } else if (!"createdAt".equals(sortBy) && !"updatedAt".equals(sortBy) && !"id".equals(sortBy)) {
            validSortBy = "updatedAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ReturnRequestDTO> returnRequests = orderManagementService.getReturnRequests(pageable);
        
        model.addAttribute("returnRequests", returnRequests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", returnRequests.getTotalPages());
        model.addAttribute("totalElements", returnRequests.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Yêu cầu trả hàng");
        model.addAttribute("activeMenu", "orders");
        
        return "manager/orders/returns";
    }
    
    @GetMapping("/returns/{id}")
    public String returnRequestDetail(@PathVariable Integer id, Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("activeSubmenu", "returns");
        
        try {
            ReturnRequestDTO returnRequest = orderManagementService.getReturnRequestDetails(id);
            model.addAttribute("returnRequest", returnRequest);
            model.addAttribute("pageTitle", "Chi tiết yêu cầu trả hàng #" + id);
            return "manager/orders/return-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manager/orders/returns";
        }
    }
    
    @PostMapping("/returns/{id}/approve")
    public String approveReturn(
            @PathVariable Integer id,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Integer deliveryTeamId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            orderManagementService.approveReturn(id, manager.getId(), note, deliveryTeamId);
            redirectAttributes.addFlashAttribute("success", "Duyệt yêu cầu trả hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/orders/returns";
    }
    
    @PostMapping("/returns/{id}/reject")
    public String rejectReturn(
            @PathVariable Integer id,
            @RequestParam String reason,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            orderManagementService.rejectReturn(id, manager.getId(), reason);
            redirectAttributes.addFlashAttribute("success", "Từ chối yêu cầu trả hàng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/orders/returns";
    }
    
    // ===== AJAX ENDPOINTS =====
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public OrderManagementDTO getOrderDetailsApi(@PathVariable Integer id) {
        return orderManagementService.getOrderDetails(id);
    }
    
    @GetMapping("/api/returns/{id}")
    @ResponseBody
    public ReturnRequestDTO getReturnRequestDetailsApi(@PathVariable Integer id) {
        return orderManagementService.getReturnRequestDetails(id);
    }
    
    // ===== BULK OPERATIONS =====
    
    @PostMapping("/pending/bulk-confirm")
    public String bulkConfirmOrders(
            @RequestParam("orderIds") String orderIds,
            @RequestParam(required = false) String note,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            String[] ids = orderIds.split(",");
            int successCount = 0;
            int errorCount = 0;
            
            for (String idStr : ids) {
                try {
                    Integer id = Integer.parseInt(idStr.trim());
                    orderManagementService.confirmOrder(id, manager.getId(), note);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                }
            }
            
            if (successCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    "Xác nhận thành công " + successCount + " đơn hàng");
            }
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "Không thể xác nhận " + errorCount + " đơn hàng");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý hàng loạt: " + e.getMessage());
        }
        
        return "redirect:/manager/orders/pending";
    }
    
    @PostMapping("/pending/bulk-cancel")
    public String bulkCancelOrders(
            @RequestParam("orderIds") String orderIds,
            @RequestParam String reason,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            String[] ids = orderIds.split(",");
            int successCount = 0;
            int errorCount = 0;
            
            for (String idStr : ids) {
                try {
                    Integer id = Integer.parseInt(idStr.trim());
                    orderManagementService.cancelOrder(id, manager.getId(), reason);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                }
            }
            
            if (successCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    "Hủy thành công " + successCount + " đơn hàng");
            }
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "Không thể hủy " + errorCount + " đơn hàng");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý hàng loạt: " + e.getMessage());
        }
        
        return "redirect:/manager/orders/pending";
    }
}
