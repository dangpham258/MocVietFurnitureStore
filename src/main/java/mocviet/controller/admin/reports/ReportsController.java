package mocviet.controller.admin.reports;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String reports(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Báo cáo & Thống kê");
        model.addAttribute("activeMenu", "reports");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/reports/admin_reports ::content";
        }
        
        return "admin/reports/admin_reports";
    }
}