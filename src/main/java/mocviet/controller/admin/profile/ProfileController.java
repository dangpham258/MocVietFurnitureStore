package mocviet.controller.admin.profile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String profile(Model model) {
        model.addAttribute("pageTitle", "Quản lý tài khoản cá nhân");
        model.addAttribute("activeMenu", "profile");
        return "admin/profile/admin_profile";
    }
}
