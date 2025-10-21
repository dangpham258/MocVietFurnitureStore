package mocviet.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.AuthResponse;
import mocviet.dto.LoginRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.RegisterRequest;
import mocviet.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            return "redirect:/dashboard";
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
            authService.login(request, response);
            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
            return "redirect:/dashboard";
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
    
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        authService.logout(response);
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!");
        return "redirect:/login";
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
    
    @PostMapping("/api/auth/logout")
    @ResponseBody
    public MessageResponse logoutApi(HttpServletResponse response) {
        authService.logout(response);
        return MessageResponse.success("Đăng xuất thành công!");
    }
}

