package mocviet.service.admin;

import mocviet.dto.admin.PasswordChangeRequest;
import mocviet.dto.admin.ProfileResponse;
import mocviet.dto.admin.ProfileUpdateRequest;
import mocviet.entity.User;

public interface AdminProfileService {
    
    ProfileResponse getCurrentProfile(User currentUser);
    
    ProfileResponse updateProfile(User currentUser, ProfileUpdateRequest request);
    
    void changePassword(User currentUser, PasswordChangeRequest request);
    
    boolean verifyCurrentPassword(User currentUser, String currentPassword);
}
