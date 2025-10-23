package mocviet.service.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.AddressRequest;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.entity.Address;
import mocviet.entity.User;
import mocviet.repository.AddressRepository;
import mocviet.repository.UserRepository;
import mocviet.service.IProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements IProfileService {
    
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Override
    public void updateProfile(ProfileUpdateRequest request) {
        User currentUser = getCurrentUserProfile();
        
        // Kiểm tra email có bị trùng không (nếu thay đổi)
        if (!currentUser.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng");
            }
        }
        
        // Cập nhật thông tin
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setGender(request.getGender());
        currentUser.setDob(request.getDob());
        currentUser.setPhone(request.getPhone());
        
        userRepository.save(currentUser);
    }
    
    @Override
    public void changePassword(PasswordChangeRequest request) {
        User currentUser = getCurrentUserProfile();
        
        // Kiểm tra mật khẩu hiện tại
        if (request.getCurrentPassword() != null && !request.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
                throw new RuntimeException("Mật khẩu hiện tại không đúng");
            }
        }
        
        // Kiểm tra mật khẩu mới và xác nhận
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("Mật khẩu không khớp");
            }
            
            currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(currentUser);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Address> getUserAddresses() {
        User currentUser = getCurrentUserProfile();
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId());
    }
    
    @Override
    public Address addAddress(AddressRequest request) {
        User currentUser = getCurrentUserProfile();
        
        // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
        if (request.getIsDefault()) {
            addressRepository.clearDefaultByUserId(currentUser.getId());
        }
        
        Address address = new Address();
        address.setUser(currentUser);
        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setIsDefault(request.getIsDefault());
        
        return addressRepository.save(address);
    }
    
    @Override
    public Address updateAddress(Integer addressId, AddressRequest request) {
        User currentUser = getCurrentUserProfile();
        
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
        if (request.getIsDefault()) {
            addressRepository.clearDefaultByUserId(currentUser.getId());
        }
        
        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setIsDefault(request.getIsDefault());
        
        return addressRepository.save(address);
    }
    
    @Override
    public void deleteAddress(Integer addressId) {
        User currentUser = getCurrentUserProfile();
        
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        addressRepository.delete(address);
    }
    
    @Override
    public void setDefaultAddress(Integer addressId) {
        User currentUser = getCurrentUserProfile();
        
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        // Bỏ mặc định của các địa chỉ khác
        addressRepository.clearDefaultByUserId(currentUser.getId());
        
        // Đặt địa chỉ này làm mặc định
        addressRepository.setDefaultByIdAndUserId(addressId, currentUser.getId());
    }
}
