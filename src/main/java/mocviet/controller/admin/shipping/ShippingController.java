package mocviet.controller.admin.shipping;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/shipping")
@RequiredArgsConstructor
public class ShippingController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String shipping(Model model) {
        model.addAttribute("pageTitle", "Quản lý phí vận chuyển");
        model.addAttribute("activeMenu", "shipping");
        return "admin/shipping/admin_shipping";
    }
}
