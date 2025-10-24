package mocviet.controller.admin.colors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/colors")
@RequiredArgsConstructor
public class ColorsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String colors(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý màu sắc");
        model.addAttribute("activeMenu", "colors");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/colors/admin_colors ::content";
        }
        
        return "admin/colors/admin_colors";
    }
}