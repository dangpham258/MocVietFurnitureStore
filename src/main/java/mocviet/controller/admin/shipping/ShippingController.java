package mocviet.controller.admin.shipping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/shipping")
@RequiredArgsConstructor
public class ShippingController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String shippingPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý phí vận chuyển");
        model.addAttribute("activeMenu", "shipping");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/shipping/admin_shipping ::content";
        }
        
        return "admin/shipping/admin_shipping";
    }
}
