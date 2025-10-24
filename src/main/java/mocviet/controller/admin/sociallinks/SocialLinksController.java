package mocviet.controller.admin.sociallinks;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/social-links")
@RequiredArgsConstructor
public class SocialLinksController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String socialLinks(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý liên kết MXH");
        model.addAttribute("activeMenu", "social-links");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/sociallinks/admin_social_links ::content";
        }
        
        return "admin/sociallinks/admin_social_links";
    }
}