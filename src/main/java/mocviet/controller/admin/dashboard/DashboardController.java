package mocviet.controller.admin.dashboard;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    @GetMapping({"", "/", "/home"})
    @PreAuthorize("hasRole('ADMIN')")
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Dashboard Admin");
        model.addAttribute("activeMenu", "dashboard");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/dashboard/admin_index ::content";
        }
        
        return "admin/dashboard/admin_index";
    }
}
