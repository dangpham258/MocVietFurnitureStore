package mocviet.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mocviet.dto.AuthResponse;
import mocviet.dto.LoginRequest;
import mocviet.dto.MessageResponse;
import mocviet.dto.RegisterRequest;
import mocviet.entity.Role;
import mocviet.entity.User;
import mocviet.repository.RoleRepository;
import mocviet.repository.UserRepository;
import mocviet.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return MessageResponse.error("Mật khẩu xác nhận không khớp");
        }
        
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return MessageResponse.error("Username đã tồn tại");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return MessageResponse.error("Email đã được sử dụng");
        }
        
        // Get USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        
        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(userRole);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        return MessageResponse.success("Đăng ký thành công! Vui lòng đăng nhập.");
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);
        
        // Set JWT as cookie if remember me is true
        if (Boolean.TRUE.equals(request.getRememberMe())) {
            Cookie cookie = new Cookie("JWT_TOKEN", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(cookie);
        }
        
        // Get user details
        User user = (User) authentication.getPrincipal();
        
        return new AuthResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }
    
    public void logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        // Clear security context
        SecurityContextHolder.clearContext();
    }
}

