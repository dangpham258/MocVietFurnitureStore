package mocviet.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
