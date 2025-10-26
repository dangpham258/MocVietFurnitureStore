package mocviet.service.admin;

import mocviet.dto.admin.UserCreateRequest;
import mocviet.dto.admin.UserResponse;
import java.util.List;

public interface AdminUserService {
    
    List<UserResponse> getAllUsers();
    
    UserResponse getUserById(Integer id);
    
    List<UserResponse> searchUsers(String keyword);
    
    UserResponse createUser(UserCreateRequest request);
    
    void toggleUserStatus(Integer userId);
    
}

