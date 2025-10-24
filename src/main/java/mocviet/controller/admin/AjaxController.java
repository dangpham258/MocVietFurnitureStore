package mocviet.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AjaxController {
    
    @GetMapping("/ajax/**")
    public String handleAjaxRequest(HttpServletRequest request) {
        // Extract the actual path from /admin/ajax/...
        String requestURI = request.getRequestURI();
        String actualPath = requestURI.replace("/admin/ajax", "/admin");
        
        // Redirect to actual controller
        return "forward:" + actualPath;
    }
}
