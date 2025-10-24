package mocviet.controller.admin.profile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String profile(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Thông tin cá nhân");
        model.addAttribute("activeMenu", "profile");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/profile/admin_profile ::content";
        }
        
        return "admin/profile/admin_profile";
    }
}