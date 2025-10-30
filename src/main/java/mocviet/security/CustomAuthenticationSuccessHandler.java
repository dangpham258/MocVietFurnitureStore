package mocviet.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import mocviet.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JwtTokenProvider tokenProvider;
    @Value("${server.servlet.session.cookie.max-age:3600}")
    private int sessionCookieMaxAgeSeconds;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        User user = (User) authentication.getPrincipal();
        String role = "CUSTOMER"; // Default role
        
        if (user.getRole() != null && user.getRole().getName() != null) {
            role = user.getRole().getName();
        }
        
        // Check remember flag (support both remember and remember-me)
        String rememberMe = request.getParameter("remember");
        if (rememberMe == null) rememberMe = request.getParameter("remember-me");
        
        boolean remember = "on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe) || "1".equals(rememberMe);

        String jwt;
        Cookie cookie;
        if (remember) {
            // 24h token + persistent cookie
            jwt = tokenProvider.generateToken(authentication, 24L * 60 * 60 * 1000);
            cookie = new Cookie("JWT_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24h
            response.addCookie(cookie);
        } else {
            // Token hết hạn theo session cookie max-age (giây)
            long durationMs = Math.max(1, sessionCookieMaxAgeSeconds) * 1000L;
            jwt = tokenProvider.generateToken(authentication, durationMs);
            cookie = new Cookie("JWT_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            // session cookie by not setting Max-Age
            response.addCookie(cookie);
        }
        
        System.out.println("JWT Cookie created: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");

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
