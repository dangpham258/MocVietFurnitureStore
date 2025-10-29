package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.User;
import mocviet.service.manager.IDeliveryAssignmentService;
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

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/manager/delivery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class DeliveryAssignmentController {
    
    private final IDeliveryAssignmentService deliveryAssignmentService;
    
    // ===== PENDING ORDERS LIST =====
    
    @GetMapping("/pending")
    public String pendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer zoneId,
            Model model) {
        
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
        if ("orderDate".equals(sortBy)) {
            validSortBy = "createdAt";
        } else if ("orderTotal".equals(sortBy)) {
            validSortBy = "totalAmount"; // Sẽ được xử lý đặc biệt trong service
        } else if ("customerName".equals(sortBy)) {
            validSortBy = "user.fullName"; // Sử dụng relationship
        } else if (!"createdAt".equals(sortBy) && !"orderTotal".equals(sortBy) && !"user.fullName".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PendingOrderDTO> orders;
        if (zoneId != null && keyword != null && !keyword.trim().isEmpty()) {
            // Lọc kết hợp zone và keyword
            orders = deliveryAssignmentService.getPendingOrdersWithZoneAndKeyword(zoneId, keyword.trim(), pageable);
        } else if (zoneId != null) {
            // Chỉ lọc theo zone
            List<PendingOrderDTO> orderList = deliveryAssignmentService.getPendingOrdersByZone(zoneId);
            // Convert to Page manually for zone filtering
            orders = new org.springframework.data.domain.PageImpl<>(orderList, pageable, orderList.size());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // Chỉ lọc theo keyword
            orders = deliveryAssignmentService.getPendingOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            // Không lọc gì
            orders = deliveryAssignmentService.getPendingOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("zones", deliveryAssignmentService.getAllZones());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedZoneId", zoneId);
        model.addAttribute("pageTitle", "Đơn hàng cần phân công");
        model.addAttribute("activeMenu", "delivery");
        
        return "manager/delivery/pending_orders";
    }
    
    // ===== ASSIGN DELIVERY TEAM =====
    
    @GetMapping("/assign/{orderId}")
    public String assignDeliveryTeamForm(@PathVariable Integer orderId, Model model) {
        try {
            if (orderId == null) {
                model.addAttribute("error", "Mã đơn hàng không hợp lệ");
                return "redirect:/manager/delivery/pending";
            }
            
            List<DeliveryTeamDTO> availableTeams = deliveryAssignmentService.getAvailableDeliveryTeams(orderId);
            String zoneName = deliveryAssignmentService.getOrderZone(orderId);
            
            model.addAttribute("orderId", orderId);
            model.addAttribute("availableTeams", availableTeams);
            model.addAttribute("zoneName", zoneName);
            
            AssignDeliveryTeamRequest assignRequest = new AssignDeliveryTeamRequest();
            assignRequest.setOrderId(orderId);
            model.addAttribute("assignRequest", assignRequest);
            model.addAttribute("pageTitle", "Phân công đội giao hàng");
            model.addAttribute("activeMenu", "delivery");
            
            return "manager/delivery/assign_team";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/manager/delivery/pending";
        }
    }
    
    @PostMapping("/assign")
    public String assignDeliveryTeam(
            @Valid @ModelAttribute AssignDeliveryTeamRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("Assign request - OrderId: " + request.getOrderId() + ", DeliveryTeamId: " + request.getDeliveryTeamId());
        
        if (bindingResult.hasErrors()) {
            System.out.println("Binding errors: " + bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/delivery/assign/" + request.getOrderId();
        }
        
        try {
            User manager = (User) authentication.getPrincipal();
            deliveryAssignmentService.assignDeliveryTeam(request, manager.getId());
            
            redirectAttributes.addFlashAttribute("success", "Phân công đội giao hàng thành công");
            return "redirect:/manager/delivery/pending";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/delivery/assign/" + request.getOrderId();
        }
    }
    
    // ===== CHANGE DELIVERY TEAM =====
    
    @GetMapping("/change/{orderId}")
    public String changeDeliveryTeamForm(@PathVariable Integer orderId, Model model) {
        try {
            if (orderId == null) {
                model.addAttribute("error", "Mã đơn hàng không hợp lệ");
                return "redirect:/manager/delivery/pending";
            }
            
            List<DeliveryTeamDTO> availableTeams = deliveryAssignmentService.getAvailableDeliveryTeams(orderId);
            String zoneName = deliveryAssignmentService.getOrderZone(orderId);
            
            model.addAttribute("orderId", orderId);
            model.addAttribute("availableTeams", availableTeams);
            model.addAttribute("zoneName", zoneName);
            
            ChangeDeliveryTeamRequest changeRequest = new ChangeDeliveryTeamRequest();
            changeRequest.setOrderId(orderId);
            model.addAttribute("changeRequest", changeRequest);
            model.addAttribute("pageTitle", "Thay đổi đội giao hàng");
            model.addAttribute("activeMenu", "delivery");
            
            return "manager/delivery/change_team";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/manager/delivery/pending";
        }
    }
    
    @PostMapping("/change")
    public String changeDeliveryTeam(
            @Valid @ModelAttribute ChangeDeliveryTeamRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/delivery/change/" + request.getOrderId();
        }
        
        try {
            User manager = (User) authentication.getPrincipal();
            deliveryAssignmentService.changeDeliveryTeam(request, manager.getId());
            
            redirectAttributes.addFlashAttribute("success", "Thay đổi đội giao hàng thành công");
            return "redirect:/manager/delivery/pending";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/delivery/change/" + request.getOrderId();
        }
    }
    
    // ===== DELIVERY TEAMS MANAGEMENT =====
    
    @GetMapping("/teams")
    public String deliveryTeams(Model model) {
        List<DeliveryTeamDTO> teams = deliveryAssignmentService.getAllDeliveryTeams();
        List<ZoneDTO> zones = deliveryAssignmentService.getAllZones();
        
        model.addAttribute("teams", teams);
        model.addAttribute("zones", zones);
        model.addAttribute("pageTitle", "Quản lý đội giao hàng");
        model.addAttribute("activeMenu", "delivery");
        
        return "manager/delivery/teams";
    }
    
    // ===== ZONE MANAGEMENT =====
    
    @GetMapping("/zones")
    public String zones(Model model) {
        List<ZoneDTO> zones = deliveryAssignmentService.getAllZones();
        
        model.addAttribute("zones", zones);
        model.addAttribute("pageTitle", "Quản lý khu vực giao hàng");
        model.addAttribute("activeMenu", "delivery");
        
        return "manager/delivery/zones";
    }
    
    // ===== AJAX ENDPOINTS =====
    
    @GetMapping("/api/teams/{orderId}")
    @ResponseBody
    public List<DeliveryTeamDTO> getAvailableTeams(@PathVariable Integer orderId) {
        return deliveryAssignmentService.getAvailableDeliveryTeams(orderId);
    }
    
    @GetMapping("/api/zone/{orderId}")
    @ResponseBody
    public String getOrderZone(@PathVariable Integer orderId) {
        return deliveryAssignmentService.getOrderZone(orderId);
    }
    
    @GetMapping("/api/order/{orderId}")
    @ResponseBody
    public PendingOrderDTO getOrderDetails(@PathVariable Integer orderId) {
        try {
            if (orderId == null) {
                throw new RuntimeException("Mã đơn hàng không hợp lệ");
            }
            
            return deliveryAssignmentService.getOrderDetails(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy chi tiết đơn hàng: " + e.getMessage());
        }
    }
    
    @GetMapping("/recent")
    public String recentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Validate và map sortBy field
        String validSortBy = sortBy;
        if ("orderDate".equals(sortBy)) {
            validSortBy = "createdAt";
        } else if ("orderTotal".equals(sortBy)) {
            validSortBy = "totalAmount"; // Sẽ được xử lý đặc biệt trong service
        } else if ("customerName".equals(sortBy)) {
            validSortBy = "user.fullName"; // Sử dụng relationship
        } else if (!"createdAt".equals(sortBy) && !"orderTotal".equals(sortBy) && !"user.fullName".equals(sortBy)) {
            validSortBy = "createdAt"; // Default fallback
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PendingOrderDTO> orders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            orders = deliveryAssignmentService.getAllRecentOrdersWithKeyword(keyword.trim(), pageable);
        } else {
            orders = deliveryAssignmentService.getAllRecentOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("zones", deliveryAssignmentService.getAllZones());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Tất cả đơn hàng gần đây");
        model.addAttribute("activeMenu", "delivery");
        
        return "manager/delivery/recent_orders";
    }
    
    
    
}
