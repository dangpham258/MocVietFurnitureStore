package mocviet.service.customer;

import mocviet.dto.AddressRequest;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.entity.Address;
import mocviet.entity.User;

import java.util.List;

public interface IProfileService {
    
    /**
     * Lấy thông tin profile của user hiện tại
     */
    User getCurrentUserProfile();
    
    /**
     * Cập nhật thông tin profile
     */
    void updateProfile(ProfileUpdateRequest request);
    
    /**
     * Thay đổi mật khẩu
     */
    void changePassword(PasswordChangeRequest request);
    
    /**
     * Lấy danh sách địa chỉ của user
     */
    List<Address> getUserAddresses();
    
    /**
     * Thêm địa chỉ mới
     */
    Address addAddress(AddressRequest request);
    
    /**
     * Cập nhật địa chỉ
     */
    Address updateAddress(Integer addressId, AddressRequest request);
    
    /**
     * Xóa địa chỉ
     */
    void deleteAddress(Integer addressId);
    
    /**
     * Đặt địa chỉ làm mặc định
     */
    void setDefaultAddress(Integer addressId);
}
