package mocviet.security;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customSuccessHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean cung cấp cơ chế xác thực (sử dụng UserDetailsService và PasswordEncoder)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }
    
    /**
     * Bean quản lý quá trình xác thực
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean cấu hình chuỗi Filter bảo mật chính
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF (Thường dùng cho API hoặc khi có cơ chế bảo vệ khác)
            .csrf(csrf -> csrf.disable())

            // Cấu hình quyền truy cập cho các đường dẫn (URL)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // --- Các trang Public & Alias Trang chủ ---
                    "/",
                    "/index",
                    "/home",

                    // --- Xác thực & Đăng ký ---
                    "/auth/**",         // Các URL bắt đầu bằng /auth/
                    "/login",           // Trang đăng nhập
                    "/register",        // Trang đăng ký
                    "/password-reset/**", // Quy trình quên mật khẩu

                    // --- Các trang Guest có thể xem ---
                    "/products",        // Danh sách sản phẩm
                    "/products/**",     // Chi tiết sản phẩm
                    "/about",           // Trang giới thiệu
                    "/contact",         // Trang liên hệ
                    "/contact/send-message", // Endpoint xử lý form liên hệ
                    "/news",            // Danh sách tin tức
                    "/news/**",         // Chi tiết tin tức
                    "/stores",          // Trang showroom (Giả định URL)
                    "/policy/**",       // Các trang chính sách (Giả định URL)
                    "/search",          // Trang tìm kiếm (nếu có)

                    // --- Tài nguyên tĩnh (QUAN TRỌNG) ---
                    "/css/**",          // Cho phép truy cập CSS
                    "/js/**",           // Cho phép truy cập JavaScript
                    "/images/**",       // Cho phép truy cập hình ảnh (nếu lưu trong static/images)
                    "/static/**",       // Cho phép truy cập toàn bộ thư mục static

                    // --- API Công khai ---
                    "/api/auth/**",     // API login/register
                    "/api/cart/count",  // API đếm giỏ hàng cho header

                    // --- Trang lỗi mặc định ---
                    "/error"
                ).permitAll() // Cho phép tất cả các URL trên mà không cần đăng nhập

                // --- Phân quyền theo Role (QUAN TRỌNG: Viết hoa tên Role) ---
                .requestMatchers("/admin/**").hasRole("ADMIN")       // Chỉ ADMIN được vào /admin/...
                .requestMatchers("/manager/**").hasRole("MANAGER")   // Chỉ MANAGER được vào /manager/...
                .requestMatchers("/delivery/**").hasRole("DELIVERY") // Chỉ DELIVERY được vào /delivery/...
                .requestMatchers( // Các URL yêu cầu vai trò CUSTOMER
                    "/customer/**", // Các URL bắt đầu bằng /customer/
                    // Các URL dưới đây thường đã nằm trong /customer/** nhưng ghi rõ nếu cần
                    "/profile/**",
                    "/orders/**",
                    "/cart/**",
                    "/wishlist/**",
                    // Các API cần đăng nhập vai trò CUSTOMER
                    "/api/cart/add",
                    "/api/cart/update-quantity",
                    "/api/cart/remove",
                    "/api/cart/clear",
                    "/api/cart/calculate-total"
                 ).hasRole("CUSTOMER") // Chỉ CUSTOMER được truy cập

                // --- Mọi request khác ---
                .anyRequest().authenticated() // Tất cả các URL không khớp các mục trên đều yêu cầu phải đăng nhập (bất kỳ vai trò nào)
            )

            // Cấu hình quản lý Session
            .sessionManagement(session -> session
                // Tạo session khi cần thiết (ví dụ: sau khi đăng nhập)
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // Chỉ cho phép 1 session đồng thời cho mỗi user
                .maximumSessions(1)
                // true: chặn login mới, false: hủy session cũ (mặc định)
                .maxSessionsPreventsLogin(false)
            )

            // Sử dụng AuthenticationProvider đã định nghĩa ở trên
            .authenticationProvider(authenticationProvider())

            // Thêm Filter JWT vào trước Filter xử lý username/password
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // Cấu hình Form Login
            .formLogin(form -> form
                .loginPage("/login") // Đường dẫn đến trang đăng nhập custom
                .loginProcessingUrl("/login") // URL mà form đăng nhập sẽ POST đến để Spring Security xử lý
                .successHandler(customSuccessHandler) // Sử dụng handler custom sau khi login thành công (để chuyển hướng theo role)
                .failureUrl("/login?error=true") // Chuyển hướng đến URL này nếu đăng nhập thất bại
                .permitAll() // Cho phép tất cả truy cập trang đăng nhập và URL xử lý đăng nhập
            )

            // Cấu hình Logout
            .logout(logout -> logout
                .logoutUrl("/logout") // URL để kích hoạt logout
                .logoutSuccessUrl("/login?logout") // Chuyển hướng đến URL này sau khi logout thành công
                .addLogoutHandler((request, response, authentication) -> { // Thêm hành động khi logout
                    // Xóa cookie JWT
                    Cookie cookie = new Cookie("JWT_TOKEN", null);
                    cookie.setHttpOnly(true); // Cookie chỉ truy cập được bởi server
                    cookie.setPath("/");     // Cookie áp dụng cho toàn bộ domain
                    cookie.setMaxAge(0);     // Hết hạn ngay lập tức (xóa cookie)
                    response.addCookie(cookie);
                })
                .invalidateHttpSession(true) // Hủy session hiện tại
                .deleteCookies("JSESSIONID", "JWT_TOKEN") // Xóa các cookie được chỉ định
                .permitAll() // Cho phép tất cả thực hiện logout
            );

        // Trả về đối tượng SecurityFilterChain đã cấu hình
        return http.build();
    }
}
