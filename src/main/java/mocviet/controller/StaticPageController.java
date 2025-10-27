package mocviet.controller;

import lombok.RequiredArgsConstructor;
import mocviet.entity.StaticPage;
import mocviet.service.StaticPageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class StaticPageController {

    private final StaticPageService staticPageService;

    /**
     * Render static page by slug
     * Route: /{slug}
     * Example: /chinh-sach-ban-hang
     */
    @GetMapping("/{slug}")
    public String renderPage(@PathVariable String slug, Model model) {
        // List of reserved paths that should not be handled by static pages
        String[] reservedPaths = {
            "login", "register", "logout", "admin", "manager", "delivery", "customer",
            "profile", "orders", "cart", "wishlist", "api", "auth", "css", "js", "images"
        };
        
        // Check if slug is a reserved path
        for (String reserved : reservedPaths) {
            if (slug.equals(reserved) || slug.startsWith(reserved + "/")) {
                // Let other controllers handle this
                return "forward:/";
            }
        }
        
        Optional<StaticPage> pageOpt = staticPageService.getPageBySlug(slug);
        
        if (pageOpt.isEmpty()) {
            // Page not found - return home page instead of redirect
            return "redirect:/";
        }
        
        StaticPage page = pageOpt.get();
        
        // Check if page is active
        if (!page.getIsActive()) {
            // Page exists but is inactive - return home page instead of redirect
            return "redirect:/";
        }
        
        // Add page data to model
        model.addAttribute("page", page);
        model.addAttribute("pageTitle", page.getTitle());
        
        // Render the static page template
        return "static-page";
    }
}

