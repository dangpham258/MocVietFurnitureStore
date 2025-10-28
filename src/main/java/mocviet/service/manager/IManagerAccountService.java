package mocviet.service.manager;

import mocviet.dto.manager.ChangePasswordRequest;
import mocviet.dto.manager.UpdateProfileRequest;
import mocviet.entity.User;
import org.springframework.security.core.Authentication;

public interface IManagerAccountService {
    User getCurrentManager(Authentication authentication);
    void updateProfile(UpdateProfileRequest request, Authentication authentication);
    void changePassword(ChangePasswordRequest request, Authentication authentication);
}


