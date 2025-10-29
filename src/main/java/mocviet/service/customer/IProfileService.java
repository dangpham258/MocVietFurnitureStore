package mocviet.service.customer;

import mocviet.dto.customer.*;
import java.util.List;

public interface IProfileService {
    
    UserDTO getCurrentUserProfile();
    
    void updateProfile(ProfileUpdateRequest request);
    
    void changePassword(PasswordChangeRequest request);
    
    List<AddressDTO> getUserAddresses();
    
    AddressDTO addAddress(AddressRequest request);
    
    AddressDTO updateAddress(Integer addressId, AddressRequest request);
    
    void deleteAddress(Integer addressId);
    
    void setDefaultAddress(Integer addressId);

    /**
     * Lấy địa chỉ theo id của user hiện tại
     */
    AddressDTO getAddressById(Integer addressId);
}
