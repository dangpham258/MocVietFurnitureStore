package mocviet.controller.admin.categories;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoriesController {

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String categories(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý danh mục");
        model.addAttribute("activeMenu", "categories");

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/categories/admin_categories ::content";
        }

        return "admin/categories/admin_categories";
    }
}
