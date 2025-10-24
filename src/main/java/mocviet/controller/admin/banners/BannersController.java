package mocviet.controller.admin.banners;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class BannersController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String banners(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý banner");
        model.addAttribute("activeMenu", "banners");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/banners/admin_banners ::content";
        }
        
        return "admin/banners/admin_banners";
    }
}