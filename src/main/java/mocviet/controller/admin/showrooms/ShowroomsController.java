package mocviet.controller.admin.showrooms;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/showrooms")
@RequiredArgsConstructor
public class ShowroomsController {

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String showrooms(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý showroom");
        model.addAttribute("activeMenu", "showrooms");

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/showrooms/admin_showrooms ::content";
        }

        return "admin/showrooms/admin_showrooms";
    }
}