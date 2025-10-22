package mocviet.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        User user = (User) authentication.getPrincipal();
        String role = user.getRole().getName();
        
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
