package mocviet.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoriesController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String categories(Model model) {
        model.addAttribute("pageTitle", "Quản lý Danh mục");
        model.addAttribute("activeMenu", "categories");
        return "admin/categories/admin_categories";
    }
}
