package mocviet.controller.admin.showrooms;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/showrooms")
@RequiredArgsConstructor
public class ShowroomsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String showrooms(Model model) {
        model.addAttribute("pageTitle", "Quản lý showroom");
        model.addAttribute("activeMenu", "showrooms");
        return "admin/showrooms/admin_showrooms";
    }
}
