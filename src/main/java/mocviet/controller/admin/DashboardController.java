package mocviet.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    @GetMapping({"", "/", "/home"})
    @PreAuthorize("hasRole('ADMIN')")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Dashboard Admin");
        model.addAttribute("activeMenu", "dashboard");
        return "admin/dashboard/admin_index";
    }
}
