package mocviet.controller.admin.coupons;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class CouponsController {

    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String couponsPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý mã giảm giá");
        model.addAttribute("activeMenu", "coupons");

        // Kiểm tra có phải là request của AJAX
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/coupons/admin_coupons ::content";
        }

        return "admin/coupons/admin_coupons";
    }
}
