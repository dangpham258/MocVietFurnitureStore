package mocviet.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.*; // Import * để gọn hơn
import mocviet.entity.Banner;
import mocviet.entity.Review;
import mocviet.entity.User;
import mocviet.service.AuthService;
import mocviet.service.guest.IGuestService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IGuestService guestService;
    private static final int HOME_PRODUCT_LIMIT = 10;

    @GetMapping("/")
    public String home(Model model) {
        try { // Thêm try-catch để bắt lỗi dễ hơn
            List<Banner> banners = guestService.getActiveBanners();
            List<ProductCardDTO> featuredProducts = guestService.getFeaturedProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> newestProducts = guestService.getNewestProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> discountedProducts = guestService.getTopDiscountedProducts(HOME_PRODUCT_LIMIT);
            List<ProductCardDTO> bestSellingProducts = guestService.getBestSellingProducts(HOME_PRODUCT_LIMIT);
            List<Review> bestReviews = guestService.getBestReviews(HOME_PRODUCT_LIMIT);

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
            // Nên kiểm tra kiểu principal trước khi ép kiểu
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                String role = user.getRole() != null ? user.getRole().getName() : "CUSTOMER"; // Xử lý role null
                switch (role) {
                    case "ADMIN": return "redirect:/admin";
                    case "MANAGER": return "redirect:/manager"; // Đảm bảo URL này tồn tại
                    case "DELIVERY": return "redirect:/delivery"; // Đảm bảo URL này tồn tại
                    case "CUSTOMER": default: return "redirect:/";
                }
            } else {
                 // Nếu principal không phải User (ví dụ: chỉ là username String), chuyển về trang chủ
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
             return "redirect:/dashboard"; // Hoặc redirect:/ nếu dashboard chưa làm
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    // Tạm thời comment trang dashboard nếu chưa dùng
    /*
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            return "dashboard";
        }
        return "redirect:/login";
    }
    */

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        // Kiểm tra mật khẩu khớp thủ công nếu cần (Validation annotation có thể chưa đủ)
        if (!request.getPassword().equals(request.getConfirmPassword())) {
             result.rejectValue("confirmPassword", "Match", "Mật khẩu xác nhận không khớp");
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
         // Thêm kiểm tra mật khẩu khớp cho API
         if (!request.getPassword().equals(request.getConfirmPassword())) {
            return MessageResponse.error("Mật khẩu xác nhận không khớp");
         }
        return authService.register(request);
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public MessageResponse logoutApi(HttpServletResponse response) {
        authService.logout(response);
        return MessageResponse.success("Đăng xuất thành công!");
    }
}