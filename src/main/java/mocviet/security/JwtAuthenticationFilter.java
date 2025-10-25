package mocviet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Chỉ xử lý JWT nếu chưa có authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String jwt = getJwtFromRequest(request);
                
                if (jwt != null) {
                    System.out.println("JWT found: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
                    System.out.println("Token valid: " + tokenProvider.validateToken(jwt));
                    
                    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                        String username = tokenProvider.getUsernameFromToken(jwt);
                        System.out.println("JWT Auth success: " + username);
                        
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    // Only log for protected endpoints to reduce noise
                    String requestURI = request.getRequestURI();
                    if (isProtectedEndpoint(requestURI)) {
                        System.out.println("No JWT Token found for: " + requestURI);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        // Try to get token from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Try to get token from cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    private boolean isProtectedEndpoint(String requestURI) {
        // Public endpoints that don't need JWT
        if (requestURI.equals("/") || 
            requestURI.equals("/login") || 
            requestURI.equals("/register") ||
            requestURI.equals("/favicon.ico") ||
            requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") ||
            requestURI.startsWith("/static/") ||
            requestURI.startsWith("/auth/")) {
            return false;
        }
        
        // All other endpoints are considered protected
        return true;
    }
}

