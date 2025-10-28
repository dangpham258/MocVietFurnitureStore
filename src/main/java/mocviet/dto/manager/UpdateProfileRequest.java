package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải có từ 2-50 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", 
             message = "Họ tên chỉ được chứa chữ cái và khoảng trắng")
    private String fullName;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    private String phone;
    
    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;
    
    @Past(message = "Ngày sinh phải trước ngày hiện tại")
    private LocalDate dob;
}
