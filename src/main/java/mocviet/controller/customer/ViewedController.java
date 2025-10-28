package mocviet.controller.customer;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.ViewedItemDTO;
import mocviet.service.customer.IViewedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer/viewed")
@RequiredArgsConstructor
public class ViewedController {

    private final IViewedService viewedService;

    @GetMapping
    public String recentViewedPage(Model model) {
        List<ViewedItemDTO> items = viewedService.getRecentViewedForCurrentUser(20);
        model.addAttribute("items", items);
        model.addAttribute("count", items.size());
        return "customer/viewed";
    }
}


