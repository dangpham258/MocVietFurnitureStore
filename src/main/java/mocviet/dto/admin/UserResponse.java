package mocviet.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String roleName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

