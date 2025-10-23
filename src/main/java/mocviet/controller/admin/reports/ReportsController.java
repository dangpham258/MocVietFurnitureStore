package mocviet.controller.admin.reports;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String reports(Model model) {
        model.addAttribute("pageTitle", "Báo cáo & thống kê");
        model.addAttribute("activeMenu", "reports");
        return "admin/reports/admin_reports";
    }
}
