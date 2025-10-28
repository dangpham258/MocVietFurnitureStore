package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.AddressDTO;
import mocviet.dto.customer.AddressRequest;
import mocviet.dto.customer.PasswordChangeRequest;
import mocviet.dto.customer.ProfileUpdateRequest;
import mocviet.dto.customer.UserDTO;
import mocviet.entity.Address;
import mocviet.entity.User;
import mocviet.repository.AddressRepository;
import mocviet.repository.UserRepository;
import mocviet.service.customer.IProfileService;
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
    public UserDTO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        return dto;
    }
    
    @Override
    public void updateProfile(ProfileUpdateRequest request) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
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
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
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
    public List<AddressDTO> getUserAddresses() {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId())
                .stream().map(this::mapToAddressDTO).toList();
    }
    
    @Override
    public AddressDTO addAddress(AddressRequest request) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Kiểm tra giới hạn 5 địa chỉ
        long addressCount = addressRepository.countByUserId(currentUser.getId());
        if (addressCount >= 5) {
            throw new RuntimeException("Mỗi khách hàng chỉ được có tối đa 5 địa chỉ nhận hàng");
        }
        
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
        
        Address saved = addressRepository.save(address);
        return mapToAddressDTO(saved);
    }
    
    @Override
    public AddressDTO updateAddress(Integer addressId, AddressRequest request) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
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
        
        Address saved = addressRepository.save(address);
        return mapToAddressDTO(saved);
    }
    
    @Override
    public void deleteAddress(Integer addressId) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        addressRepository.delete(address);
    }
    
    @Override
    public void setDefaultAddress(Integer addressId) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        // Bỏ mặc định của các địa chỉ khác
        addressRepository.clearDefaultByUserId(currentUser.getId());
        
        // Đặt địa chỉ này làm mặc định
        addressRepository.setDefaultByIdAndUserId(addressId, currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Integer addressId) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findByIdAndUserId(addressId, currentUser.getId())
                .orElse(null);
        return address != null ? mapToAddressDTO(address) : null;
    }

    private AddressDTO mapToAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setReceiverName(address.getReceiverName());
        dto.setPhone(address.getPhone());
        dto.setAddressLine(address.getAddressLine());
        dto.setDistrict(address.getDistrict());
        dto.setCity(address.getCity());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }
}
