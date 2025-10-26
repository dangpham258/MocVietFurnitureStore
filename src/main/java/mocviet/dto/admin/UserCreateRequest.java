package mocviet.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {
    
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 60, message = "Username phải từ 3 đến 60 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username chỉ chứa chữ cái, số và dấu gạch dưới")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 120, message = "Email không được quá 120 ký tự")
    private String email;
    
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 120, message = "Họ và tên phải từ 2 đến 120 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    @Pattern(regexp = "^$|^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;
    
    @Size(max = 10, message = "Giới tính không được quá 10 ký tự")
    @Pattern(regexp = "^$|^(Nam|Nữ|Khác)$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;
    
    private java.time.LocalDate dob;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số")
    private String password;
    
    @NotBlank(message = "Vai trò không được để trống")
    private String roleName;
    
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive = true;
}

