package mocviet.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Báo cáo & Thống kê");
        model.addAttribute("activeMenu", "reports");
        return "admin/reports/admin_reports";
    }
}
