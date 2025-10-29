package mocviet.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.AuthResponse;
import mocviet.dto.BannerDTO;
import mocviet.dto.LoginRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.ProductCardDTO;
import mocviet.dto.RegisterRequest;
import mocviet.dto.ReviewDTO;
import mocviet.dto.UserDetailsDTO;
import mocviet.service.AuthService;
import mocviet.service.guest.IGuestService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IGuestService guestService;
    private static final int HOME_PRODUCT_LIMIT = 10;

    @GetMapping("/")
    public String home(Model model) {
        try { // Thêm try-catch để bắt lỗi dễ hơn
            List<BannerDTO> banners = guestService.getActiveBanners();
            List<ProductCardDTO> featuredProducts = guestService.getFeaturedProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> newestProducts = guestService.getNewestProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> discountedProducts = guestService.getTopDiscountedProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> bestSellingProducts = guestService.getBestSellingProducts(HOME_PRODUCT_LIMIT);
            List<ReviewDTO> bestReviews = guestService.getBestReviews(HOME_PRODUCT_LIMIT);

            model.addAttribute("banners", banners);
            model.addAttribute("featuredProducts", featuredProducts);
            model.addAttribute("newestProducts", newestProducts);
            model.addAttribute("discountedProducts", discountedProducts);
            model.addAttribute("bestSellingProducts", bestSellingProducts);
            model.addAttribute("bestReviews", bestReviews);

        } catch (Exception e) {
             System.err.println("Lỗi khi lấy dữ liệu trang chủ: " + e.getMessage()); // In lỗi ra console
             e.printStackTrace(); // In stack trace để debug
            // Có thể thêm thuộc tính lỗi vào model để hiển thị trên trang
            // model.addAttribute("homePageError", "Không thể tải dữ liệu trang chủ. Vui lòng thử lại sau.");
        }
        return "index"; // Luôn trả về index, dù có lỗi hay không
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // User đã đăng nhập, chuyển hướng đến trang phù hợp
            String role = getRoleFromAuthentication(auth);
            
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
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!"); // Thông báo logout
        return "redirect:/login?logout"; // Thêm param logout để trang login biết là vừa logout
    }

    // APIs giữ nguyên
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
    
    @GetMapping("/api/auth/status")
    @ResponseBody
    public Map<String, Object> checkAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            response.put("authenticated", true);
            
            // Chuyển đổi thông tin auth sang UserDetailsDTO
            UserDetailsDTO userDetails = getUserDetailsFromAuthentication(auth);
            
            response.put("user", Map.of(
                "id", userDetails.getId(),
                "username", userDetails.getUsername(),
                "fullName", userDetails.getFullName(),
                "role", userDetails.getRole()
            ));
        } else {
            response.put("authenticated", false);
        }
        
        return response;
    }
    
    /**
     * Lấy role từ Authentication mà không cần cast trực tiếp sang User entity
     */
    private String getRoleFromAuthentication(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
            return "CUSTOMER";
        }
        
        // Lấy role từ GrantedAuthority
        GrantedAuthority authority = auth.getAuthorities().iterator().next();
        String authorityName = authority.getAuthority();
        
        // Loại bỏ prefix "ROLE_" nếu có
        return authorityName.startsWith("ROLE_") 
            ? authorityName.substring(5) 
            : authorityName;
    }
    
    /**
     * Chuyển đổi Authentication thành UserDetailsDTO
     * Tránh phụ thuộc trực tiếp vào User entity trong controller
     */
    private UserDetailsDTO getUserDetailsFromAuthentication(Authentication auth) {
        String username = auth.getName();
        String role = getRoleFromAuthentication(auth);
        
        // Vẫn cần cast User entity để lấy id và fullName
        // nhưng chỉ trong trường hợp thực sự cần thiết
        mocviet.entity.User userEntity = auth.getPrincipal() instanceof mocviet.entity.User 
            ? (mocviet.entity.User) auth.getPrincipal() 
            : null;
        
        return UserDetailsDTO.builder()
                .id(userEntity != null ? userEntity.getId() : null)
                .username(username)
                .fullName(userEntity != null ? userEntity.getFullName() : null)
                .role(role)
                .build();
    }
}
