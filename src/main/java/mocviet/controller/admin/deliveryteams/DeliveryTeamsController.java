package mocviet.controller.admin.deliveryteams;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/delivery-teams")
@RequiredArgsConstructor
public class DeliveryTeamsController {
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String deliveryTeams(Model model) {
        model.addAttribute("pageTitle", "Quản lý đội giao hàng");
        model.addAttribute("activeMenu", "delivery-teams");
        return "admin/deliveryteams/admin_delivery_teams";
    }
}
