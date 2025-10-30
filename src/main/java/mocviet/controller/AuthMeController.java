package mocviet.controller;

import lombok.RequiredArgsConstructor;
import mocviet.entity.User;
import mocviet.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthMeController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Object principal = authentication.getPrincipal();
        Map<String, Object> body = new HashMap<>();
        if (principal instanceof User user) {
            body.put("id", user.getId());
            body.put("username", user.getUsername());
            body.put("fullName", user.getFullName());
            body.put("role", user.getRole() != null ? user.getRole().getName() : null);
        } else if (principal instanceof UserDetails userDetails) {
            // Tra cứu entity để lấy id chuẩn
            User entity = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (entity == null) {
                return ResponseEntity.status(401).build();
            }
            body.put("id", entity.getId());
            body.put("username", entity.getUsername());
            body.put("fullName", entity.getFullName());
            body.put("role", entity.getRole() != null ? entity.getRole().getName() : null);
        } else {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(body);
    }
}


