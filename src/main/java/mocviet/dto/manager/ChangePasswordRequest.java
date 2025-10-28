package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;
    
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", 
             message = "Mật khẩu phải có ít nhất 1 chữ thường, 1 chữ hoa và 1 số")
    private String newPassword;
    
    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
