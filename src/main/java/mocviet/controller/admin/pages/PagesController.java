package mocviet.controller.admin.pages;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/pages")
@RequiredArgsConstructor
public class PagesController {

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String pages(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý trang tĩnh");
        model.addAttribute("activeMenu", "pages");

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/pages/admin_pages ::content";
        }

        return "admin/pages/admin_pages";
    }
}