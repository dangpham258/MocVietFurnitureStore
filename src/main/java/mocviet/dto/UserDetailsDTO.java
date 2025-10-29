package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin user cơ bản từ authentication
 * Sử dụng thay vì trực tiếp truy cập User entity trong controller
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String role;
}

