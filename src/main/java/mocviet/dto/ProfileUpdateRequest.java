package mocviet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 120, message = "Họ và tên không được vượt quá 120 ký tự")
    private String fullName;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 120, message = "Email không được vượt quá 120 ký tự")
    private String email;
    
    @Size(max = 10, message = "Giới tính không được vượt quá 10 ký tự")
    private String gender;
    
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @MinAge(message = "Tuổi phải từ 15 tuổi trở lên")
    private LocalDate dob;
    
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    @PhoneNumber(message = "Số điện thoại chỉ được chứa số")
    private String phone;
}
