package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.UserCreateRequest;
import mocviet.dto.admin.UserResponse;

public interface AdminUserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);

    List<UserResponse> searchUsers(String keyword);

    UserResponse createUser(UserCreateRequest request);

    void toggleUserStatus(Integer userId);

}

