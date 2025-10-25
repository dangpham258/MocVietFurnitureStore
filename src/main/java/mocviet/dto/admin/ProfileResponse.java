package mocviet.dto.admin;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private Boolean isActive;
    private String roleName;
    private LocalDateTime createdAt;
}
