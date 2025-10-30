package mocviet.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mocviet.repository.ShowroomRepository;

@Controller
public class ShowroomController {

    private final ShowroomRepository showroomRepository;

    public ShowroomController(ShowroomRepository showroomRepository) {
        this.showroomRepository = showroomRepository;
    }

    @GetMapping("/showroom")
    public String showrooms(Model model) {
        model.addAttribute("showrooms", showroomRepository.findByIsActiveTrueOrderByCreatedAtDesc());
        model.addAttribute("pageTitle", "Showroom");
        model.addAttribute("pageDescription", "Xem thông tin địa chỉ các cửa hàng trưng bày");
        return "guest/showroom";
    }
}