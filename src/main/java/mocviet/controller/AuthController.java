package mocviet.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.AuthResponse;
import mocviet.dto.LoginRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.RegisterRequest;
import mocviet.entity.User;
import mocviet.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ===== VIEW CONTROLLERS =====

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // User đã đăng nhập, chuyển hướng đến trang phù hợp
            User user = (User) auth.getPrincipal();
            String role = user.getRole().getName();

            switch (role) {
                case "ADMIN":
                    return "redirect:/admin";
                case "MANAGER":
                    return "redirect:/manager";
                case "DELIVERY":
                    return "redirect:/delivery";
                case "CUSTOMER":
                default:
                    return "redirect:/";
            }
        }

        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            return "dashboard";
        }
        return "redirect:/login";
    }

    // ===== POST ACTIONS =====

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest request,
                       BindingResult result,
                       HttpServletResponse response,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            AuthResponse authResponse = authService.login(request, response);
            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");

            // Redirect theo role
            String role = authResponse.getRole();
            switch (role) {
                case "ADMIN":
                    return "redirect:/admin";
                case "MANAGER":
                    return "redirect:/manager";
                case "DELIVERY":
                    return "redirect:/delivery";
                case "CUSTOMER":
                default:
                    return "redirect:/";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "auth/login";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        MessageResponse response = authService.register(request);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("success", response.getMessage());
            return "redirect:/login";
        } else {
            model.addAttribute("error", response.getMessage());
            return "auth/register";
        }
    }

    // ===== REST API ENDPOINTS =====

    @PostMapping("/api/auth/login")
    @ResponseBody
    public AuthResponse loginApi(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public MessageResponse registerApi(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}

