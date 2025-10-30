package mocviet.controller.admin;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "mocviet.controller.admin")
public class AdminControllerAdvice {

	// Giúp cho js luôn luôn được cập nhật mới nhất
    @ModelAttribute("jsVersion")
    public Long jsVersion() {
        return System.currentTimeMillis();
    }
}
