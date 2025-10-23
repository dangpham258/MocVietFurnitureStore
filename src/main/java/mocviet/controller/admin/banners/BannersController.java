package mocviet.controller.admin.banners;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
public class BannersController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String banners(Model model) {
        model.addAttribute("pageTitle", "Quản lý banner");
        model.addAttribute("activeMenu", "banners");
        return "admin/banners/admin_banners";
    }
}
