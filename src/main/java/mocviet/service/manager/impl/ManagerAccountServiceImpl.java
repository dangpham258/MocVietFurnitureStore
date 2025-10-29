package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.ChangePasswordRequest;
import mocviet.dto.manager.UpdateProfileRequest;
import mocviet.entity.User;
import mocviet.repository.UserRepository;
import mocviet.service.manager.IManagerAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ManagerAccountServiceImpl implements IManagerAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentManager(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request, Authentication authentication) {
        User currentUser = getCurrentManager(authentication);
        if (request.getDob() != null) {
            int age = Period.between(request.getDob(), LocalDate.now()).getYears();
            if (age < 18 || age > 100) throw new IllegalArgumentException("Tuổi phải từ 18-100");
        }
        if (!request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), currentUser.getId())) {
                throw new IllegalArgumentException("Email này đã được sử dụng");
            }
        }
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhone(request.getPhone() != null && !request.getPhone().trim().isEmpty() ? request.getPhone() : null);
        currentUser.setGender(request.getGender() != null && !request.getGender().trim().isEmpty() ? request.getGender() : null);
        currentUser.setDob(request.getDob());
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, Authentication authentication) {
        User currentUser = getCurrentManager(authentication);
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }
        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}


