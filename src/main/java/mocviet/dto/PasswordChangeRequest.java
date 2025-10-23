package mocviet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    
    private String currentPassword;
    
    @Size(min = 6, max = 20, message = "Mật khẩu mới phải có từ 6 đến 20 ký tự")
    private String newPassword;
    
    private String confirmPassword;
}
