package mocviet.controller.admin.colors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/colors")
@RequiredArgsConstructor
public class ColorsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String colors(Model model) {
        model.addAttribute("pageTitle", "Quản lý màu sắc");
        model.addAttribute("activeMenu", "colors");
        return "admin/colors/admin_colors";
    }
}
