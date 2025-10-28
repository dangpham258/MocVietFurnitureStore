package mocviet.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Category;
import mocviet.entity.User;
import mocviet.repository.CategoryRepository;
import mocviet.service.manager.IInventoryManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý tồn kho cho Manager
 * Route: /manager/inventory/*
 */
@Controller
@RequestMapping("/manager/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class InventoryManagementController {
    
    private final IInventoryManagementService inventoryService;
    private final CategoryRepository categoryRepository;
    
    // ===== TRANG CHỦ QUẢN LÝ TỒN KHO =====
    
    @GetMapping("")
    public String inventoryHome() {
        return "redirect:/manager/inventory/alerts";
    }
    
    // ===== UC-MGR-INV-ViewStockAlerts: XEM CẢNH BÁO TỒN KHO =====
    
    @GetMapping("/alerts")
    public String stockAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "inventory");
        model.addAttribute("activeSubmenu", "alerts");
        
        // Reset page về 0 khi có filter mới
        if ((alertType != null && !alertType.isEmpty()) || 
            (keyword != null && !keyword.trim().isEmpty())) {
            page = 0;
        }
        
        // Validate và clean keyword
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Lấy dữ liệu tổng quan
        StockSummaryDTO summary = inventoryService.getStockSummary();
        
        // Tạo pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Lấy danh sách cảnh báo
        Page<StockAlertDTO> alerts = inventoryService.getStockAlerts(pageable, alertType, keyword);
        
        // Add attributes
        model.addAttribute("summary", summary);
        model.addAttribute("alerts", alerts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", alerts.getTotalPages());
        model.addAttribute("totalElements", alerts.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("alertType", alertType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Cảnh báo tồn kho");
        
        return "manager/inventory/alerts";
    }
    
    // ===== UC-MGR-INV-UpdateStock: CẬP NHẬT TỒN KHO =====
    
    @GetMapping("/update/{variantId}")
    public String showUpdateForm(@PathVariable Integer variantId, Model model) {
        try {
            StockAlertDTO variant = inventoryService.getVariantDetails(variantId);
            
            UpdateStockRequest request = new UpdateStockRequest();
            request.setVariantId(variantId);
            request.setNewStockQty(variant.getStockQty());
            
            model.addAttribute("variant", variant);
            model.addAttribute("request", request);
            model.addAttribute("pageTitle", "Cập nhật tồn kho");
            model.addAttribute("activeMenu", "inventory");
            
            return "manager/inventory/update-stock";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/manager/inventory/alerts";
        }
    }
    
    @PostMapping("/update")
    public String updateStock(
            @Valid @ModelAttribute("request") UpdateStockRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
            return "redirect:/manager/inventory/update/" + request.getVariantId();
        }
        
        try {
            User manager = (User) authentication.getPrincipal();
            inventoryService.updateStock(request, manager.getId());
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật tồn kho thành công");
            return "redirect:/manager/inventory/alerts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/inventory/update/" + request.getVariantId();
        }
    }
    
    // API endpoint cho AJAX update (nếu cần update nhanh từ danh sách)
    @PostMapping("/quick-update")
    @ResponseBody
    public String quickUpdateStock(
            @RequestParam Integer variantId,
            @RequestParam Integer newStockQty,
            @RequestParam(required = false) String note,
            Authentication authentication) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            
            UpdateStockRequest request = new UpdateStockRequest();
            request.setVariantId(variantId);
            request.setNewStockQty(newStockQty);
            request.setNote(note);
            
            inventoryService.updateStock(request, manager.getId());
            
            return "{\"success\": true, \"message\": \"Cập nhật thành công\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    // ===== UC-MGR-INV-ViewStockReport: XEM BÁO CÁO TỒN KHO =====
    
    @GetMapping("/report")
    public String stockReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "stockQty") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String stockLevel,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "inventory");
        model.addAttribute("activeSubmenu", "report");
        
        // Không reset page khi chuyển trang
        // Chỉ reset page khi có filter mới (được xử lý ở frontend)
        
        // Validate và clean keyword
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Tạo pageable (không dùng sort vì đã xử lý trong service)
        Pageable pageable = PageRequest.of(page, size);
        
        // Lấy báo cáo
        Page<StockReportDTO> report = inventoryService.getStockReport(
            pageable, categoryId, stockLevel, keyword
        );
        
        // Lấy summary
        StockSummaryDTO summary = inventoryService.getStockSummary();
        
        // Add attributes
        model.addAttribute("report", report);
        model.addAttribute("summary", summary);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", report.getTotalPages());
        model.addAttribute("totalElements", report.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("stockLevel", stockLevel);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Báo cáo tồn kho");
        
        return "manager/inventory/report";
    }
    
    // ===== UC-MGR-INV-ManageLowStock: QUẢN LÝ SẢN PHẨM TỒN KHO THẤP =====
    
    @GetMapping("/low-stock")
    public String lowStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        // Set active menu for sidebar
        model.addAttribute("activeMenu", "inventory");
        model.addAttribute("activeSubmenu", "low-stock");
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        // Validate và clean keyword
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        
        // Đảm bảo page không bao giờ âm
        if (page < 0) {
            page = 0;
        }
        
        // Tạo pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Lấy danh sách sản phẩm tồn kho thấp
        Page<LowStockProductDTO> lowStockProducts = inventoryService.getLowStockProducts(pageable, keyword);
        
        // Lấy summary
        StockSummaryDTO summary = inventoryService.getStockSummary();
        
        // Add attributes
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("summary", summary);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lowStockProducts.getTotalPages());
        model.addAttribute("totalElements", lowStockProducts.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Quản lý sản phẩm tồn kho thấp");
        
        return "manager/inventory/low-stock";
    }
    
    // ===== ACTIONS: ẨN/HIỆN SẢN PHẨM =====
    
    @PostMapping("/hide/{variantId}")
    public String hideProduct(
            @PathVariable Integer variantId,
            @RequestParam(required = false) String reason,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            inventoryService.hideProduct(variantId, manager.getId(), reason);
            
            redirectAttributes.addFlashAttribute("success", "Đã ẩn sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/inventory/low-stock";
    }
    
    @PostMapping("/show/{variantId}")
    public String showProduct(
            @PathVariable Integer variantId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            User manager = (User) authentication.getPrincipal();
            inventoryService.showProduct(variantId, manager.getId());
            
            redirectAttributes.addFlashAttribute("success", "Đã hiện sản phẩm thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/manager/inventory/low-stock";
    }
    
    /**
     * Trang sản phẩm đã ẩn
     */
    @GetMapping("/hidden-products")
    public String hiddenProducts(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "12") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer categoryId,
                                @RequestParam(required = false) String stockLevel,
                                Model model) {
        try {
            // Tạo Pageable
            Pageable pageable = PageRequest.of(page, size);
            
            // Lấy danh sách sản phẩm đã ẩn
            Page<LowStockProductDTO> hiddenProducts = inventoryService.getHiddenProducts(pageable, keyword, categoryId, stockLevel);
            
            // Lấy thống kê
            Map<String, Object> summary = inventoryService.getHiddenProductsSummary();
            
            // Lấy danh sách categories
            List<Category> categories = categoryRepository.findByTypeAndIsActiveTrue(Category.CategoryType.CATEGORY);
            
            // Thêm vào model
            model.addAttribute("hiddenProducts", hiddenProducts.getContent());
            model.addAttribute("summary", summary);
            model.addAttribute("categories", categories);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", hiddenProducts.getTotalPages());
            model.addAttribute("totalElements", hiddenProducts.getTotalElements());
            model.addAttribute("hasNext", hiddenProducts.hasNext());
            model.addAttribute("hasPrevious", hiddenProducts.hasPrevious());
            model.addAttribute("size", size);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("stockLevel", stockLevel);
            model.addAttribute("pageTitle", "Sản phẩm đã ẩn");
            model.addAttribute("activeMenu", "inventory");
            model.addAttribute("activeSubmenu", "hidden-products");
            
            return "manager/inventory/hidden-products";
        } catch (Exception e) {
            // Tạo summary mặc định khi có lỗi
            Map<String, Object> defaultSummary = new HashMap<>();
            defaultSummary.put("totalHiddenProducts", 0);
            defaultSummary.put("outOfStockCount", 0);
            defaultSummary.put("lowStockCount", 0);
            
            model.addAttribute("hiddenProducts", new ArrayList<>());
            model.addAttribute("summary", defaultSummary);
            model.addAttribute("categories", new ArrayList<>());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("size", size);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("stockLevel", stockLevel);
            model.addAttribute("pageTitle", "Sản phẩm đã ẩn");
            model.addAttribute("activeMenu", "inventory");
            model.addAttribute("activeSubmenu", "hidden-products");
            model.addAttribute("error", "Lỗi khi tải danh sách sản phẩm đã ẩn: " + e.getMessage());
            return "manager/inventory/hidden-products";
        }
    }
}

