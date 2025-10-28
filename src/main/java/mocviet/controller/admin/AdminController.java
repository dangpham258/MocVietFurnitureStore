package mocviet.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.DashboardStatsDTO;
import mocviet.service.admin.DashboardService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHome(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Dashboard Admin");
        model.addAttribute("activeMenu", "dashboard");

        // Lấy dữ liệu dashboard
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/dashboard/admin_index ::content";
        }

        return "admin/dashboard/admin_index";
    }
}
