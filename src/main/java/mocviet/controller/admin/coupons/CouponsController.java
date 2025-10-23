package mocviet.controller.admin.coupons;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class CouponsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String coupons(Model model) {
        model.addAttribute("pageTitle", "Quản lý mã giảm giá");
        model.addAttribute("activeMenu", "coupons");
        return "admin/coupons/admin_coupons";
    }
}
