package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.UserCreateRequest;
import mocviet.dto.admin.UserResponse;
import mocviet.entity.Role;
import mocviet.entity.User;
import mocviet.repository.RoleRepository;
import mocviet.repository.UserRepository;
import mocviet.service.admin.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToResponse(user);
    }
    
    @Override
    public List<UserResponse> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(user -> 
                    user.getFullName() != null && user.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getUsername() != null && user.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getPhone() != null && user.getPhone().toLowerCase().contains(keyword.toLowerCase())
                )
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // Validate role - chỉ cho phép tạo MANAGER hoặc DELIVERY
        Role role = roleRepository.findByName(request.getRoleName())
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        String roleName = role.getName();
        if (!roleName.equals("MANAGER") && !roleName.equals("DELIVERY")) {
            throw new RuntimeException("Chỉ được tạo tài khoản MANAGER hoặc DELIVERY");
        }
        
        // Check username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }
        
        // Check email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        // Create user
        User user = new User();
        user.setRole(role);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDob(request.getDob());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(request.getIsActive());
        user.setCreatedAt(LocalDateTime.now());
        
        user = userRepository.save(user);
        
        return convertToResponse(user);
    }
    
    @Override
    @Transactional
    public void toggleUserStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }
    
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setDob(user.getDob());
        response.setRoleName(user.getRole() != null ? user.getRole().getName() : "UNKNOWN");
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}

