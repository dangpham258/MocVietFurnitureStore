package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.ChangePasswordRequest;
import mocviet.dto.manager.UpdateProfileRequest;
import mocviet.service.manager.IManagerAccountService;
import mocviet.service.manager.IDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    
    private final IManagerAccountService managerAccountService;
    private final IDashboardService dashboardService;
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('MANAGER')")
    public String dashboard(Model model, Authentication authentication) {
        try {
            // Lấy thống kê dashboard
            mocviet.service.manager.DashboardService.DashboardStats stats = dashboardService.getDashboardStats();
            model.addAttribute("stats", stats);
            
            // Lấy đơn hàng gần đây
            var recentOrders = dashboardService.getRecentOrders(5);
            model.addAttribute("recentOrders", recentOrders);
            
            // Lấy thông báo
            var notifications = dashboardService.getNotifications();
            model.addAttribute("notifications", notifications);
            
            model.addAttribute("pageTitle", "Dashboard Manager");
            model.addAttribute("activeMenu", "dashboard");
            model.addAttribute("username", authentication.getName());
            
            return "manager/dashboard/manager_index";
        } catch (Exception e) {
            // Nếu có lỗi, hiển thị dashboard với dữ liệu mặc định
            model.addAttribute("error", "Không thể tải dữ liệu dashboard: " + e.getMessage());
            model.addAttribute("pageTitle", "Dashboard Manager");
            model.addAttribute("activeMenu", "dashboard");
            model.addAttribute("username", authentication.getName());
            return "manager/dashboard/manager_index";
        }
    }
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('MANAGER')")
    public String showProfile(Model model, Authentication authentication) {
        var user = managerAccountService.getCurrentManager(authentication);
        
        // Pre-fill form with current user data
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setFullName(user.getFullName());
        updateRequest.setEmail(user.getEmail());
        updateRequest.setPhone(user.getPhone());
        updateRequest.setGender(user.getGender());
        updateRequest.setDob(user.getDob());
        
        model.addAttribute("pageTitle", "Quản lý tài khoản");
        model.addAttribute("activeMenu", "profile");
        model.addAttribute("user", user);
        model.addAttribute("updateProfileRequest", updateRequest);
        return "manager/account/profile";
    }
    
    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('MANAGER')")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request,
                               BindingResult result,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            var user = managerAccountService.getCurrentManager(authentication);
            model.addAttribute("user", user);
            model.addAttribute("activeMenu", "profile");
            model.addAttribute("pageTitle", "Quản lý tài khoản");
            return "manager/account/profile";
        }
        
        try {
            managerAccountService.updateProfile(request, authentication);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/manager/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            var user = managerAccountService.getCurrentManager(authentication);
            model.addAttribute("user", user);
            model.addAttribute("activeMenu", "profile");
            model.addAttribute("pageTitle", "Quản lý tài khoản");
            return "manager/account/profile";
        }
    }
    
    @GetMapping("/change-password")
    @PreAuthorize("hasRole('MANAGER')")
    public String showChangePassword(Model model) {
        model.addAttribute("pageTitle", "Đổi mật khẩu");
        model.addAttribute("activeMenu", "change-password");
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "manager/account/change-password";
    }
    
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('MANAGER')")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                BindingResult result,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Đổi mật khẩu");
            model.addAttribute("activeMenu", "change-password");
            return "manager/account/change-password";
        }
        
        try {
            managerAccountService.changePassword(request, authentication);
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/manager/change-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Đổi mật khẩu");
            model.addAttribute("activeMenu", "change-password");
            return "manager/account/change-password";
        }
    }
}
