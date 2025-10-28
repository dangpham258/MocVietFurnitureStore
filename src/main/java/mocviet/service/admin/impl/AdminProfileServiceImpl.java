package mocviet.service.admin.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.admin.PasswordChangeRequest;
import mocviet.dto.admin.ProfileResponse;
import mocviet.dto.admin.ProfileUpdateRequest;
import mocviet.entity.User;
import mocviet.repository.UserRepository;
import mocviet.service.admin.AdminProfileService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminProfileServiceImpl implements AdminProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentProfile(User currentUser) {
        log.info("Getting profile for user: {}", currentUser.getUsername());

        return ProfileResponse.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .fullName(currentUser.getFullName())
                .phone(currentUser.getPhone())
                .gender(currentUser.getGender())
                .dob(currentUser.getDob())
                .isActive(currentUser.getIsActive())
                .roleName(currentUser.getRole().getName())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }

    @Override
    public ProfileResponse updateProfile(User currentUser, ProfileUpdateRequest request) {
        log.info("Updating profile for user: {}", currentUser.getUsername());
        log.info("Request data - Email: {}, FullName: {}, Phone: {}, Gender: {}, DOB: {}",
                request.getEmail(), request.getFullName(), request.getPhone(), request.getGender(), request.getDob());

        // Kiểm tra email uniqueness nếu email thay đổi
        if (!currentUser.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), currentUser.getId())) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
            }
        }

        // Cập nhật thông tin
        currentUser.setEmail(request.getEmail());
        currentUser.setFullName(request.getFullName());
        currentUser.setPhone(request.getPhone());
        currentUser.setGender(request.getGender());
        currentUser.setDob(request.getDob());

        log.info("Updated user DOB: {}", currentUser.getDob());

        // Lưu vào database
        User savedUser = userRepository.save(currentUser);

        log.info("Profile updated successfully for user: {}", savedUser.getUsername());
        log.info("Saved user DOB: {}", savedUser.getDob());

        return ProfileResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .gender(savedUser.getGender())
                .dob(savedUser.getDob())
                .isActive(savedUser.getIsActive())
                .roleName(savedUser.getRole().getName())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public void changePassword(User currentUser, PasswordChangeRequest request) {
        log.info("Changing password for user: {}", currentUser.getUsername());

        // Kiểm tra mật khẩu hiện tại
        if (!verifyCurrentPassword(currentUser, request.getCurrentPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        currentUser.setPasswordHash(encodedPassword);

        // Lưu vào database
        userRepository.save(currentUser);

        log.info("Password changed successfully for user: {}", currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyCurrentPassword(User currentUser, String currentPassword) {
        return passwordEncoder.matches(currentPassword, currentUser.getPasswordHash());
    }
}
