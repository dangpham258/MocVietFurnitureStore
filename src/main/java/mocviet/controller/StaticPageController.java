package mocviet.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import mocviet.entity.StaticPage;
import mocviet.service.StaticPageService;

@Controller
@RequiredArgsConstructor
public class StaticPageController {

    private final StaticPageService staticPageService;

    /**
     * Render static page theo đường dẫn slug
     * Route: /{slug}
     * Example: /chinh-sach-ban-hang
     */
    @GetMapping("/{slug}")
    public String renderPage(@PathVariable String slug, Model model) {
        // Danh sách các đường dẫn được giữ nguyên bởi các controller khác
        String[] reservedPaths = {
            "login", "register", "logout", "admin", "manager", "delivery", "customer",
            "profile", "orders", "cart", "wishlist", "api", "auth", "css", "js", "images"
        };

        // Kiểm tra xem slug có phải là đường dẫn được giữ nguyên không
        for (String reserved : reservedPaths) {
            if (slug.equals(reserved) || slug.startsWith(reserved + "/")) {
                // Cho phép các controller khác xử lý
                return "forward:/";
            }
        }

        Optional<StaticPage> pageOpt = staticPageService.getPageBySlug(slug);

        if (pageOpt.isEmpty()) {
            // Trang không tồn tại - chuyển hướng đến trang chủ thay vì redirect
            return "redirect:/";
        }

        StaticPage page = pageOpt.get();

        // Kiểm tra xem trang có hoạt động không
        if (!page.getIsActive()) {
            // Trang tồn tại nhưng không hoạt động - chuyển hướng đến trang chủ thay vì redirect
            return "redirect:/";
        }

        // Thêm dữ liệu trang vào model
        model.addAttribute("page", page);
        model.addAttribute("pageTitle", page.getTitle());

        // Render template trang tĩnh
        return "static-page";
    }
}

