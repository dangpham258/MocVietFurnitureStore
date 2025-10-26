package mocviet.controller.admin.deliveryteams;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/delivery-teams")
@RequiredArgsConstructor
public class DeliveryTeamsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String deliveryTeamsPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Quản lý đội giao hàng");
        model.addAttribute("activeMenu", "delivery-teams");
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/deliveryteams/admin_delivery_teams ::content";
        }
        
        return "admin/deliveryteams/admin_delivery_teams";
    }
}
