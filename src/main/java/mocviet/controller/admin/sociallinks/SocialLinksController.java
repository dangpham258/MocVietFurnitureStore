package mocviet.controller.admin.sociallinks;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/social-links")
@RequiredArgsConstructor
public class SocialLinksController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String socialLinks(Model model) {
        model.addAttribute("pageTitle", "Quản lý mạng xã hội");
        model.addAttribute("activeMenu", "social-links");
        return "admin/sociallinks/admin_social_links";
    }
}
