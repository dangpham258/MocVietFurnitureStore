package mocviet.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mocviet.entity.User;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

        User user = (User) authentication.getPrincipal();
        String role = user.getRole().getName();

        // Check if remember me is checked
        String rememberMe = request.getParameter("rememberMe");
        System.out.println("🔍 Remember Me parameter: " + rememberMe);

        if ("true".equals(rememberMe) || "on".equals(rememberMe)) {
            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Set JWT as cookie
            Cookie cookie = new Cookie("JWT_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(cookie);

            System.out.println("JWT Cookie created: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
        } else {
            System.out.println("Remember Me not checked, no JWT cookie created");
        }

        switch (role) {
            case "ADMIN":
                response.sendRedirect("/admin");
                break;
            case "MANAGER":
                response.sendRedirect("/manager");
                break;
            case "DELIVERY":
                response.sendRedirect("/delivery");
                break;
            case "CUSTOMER":
            default:
                response.sendRedirect("/");
                break;
        }
    }
}
