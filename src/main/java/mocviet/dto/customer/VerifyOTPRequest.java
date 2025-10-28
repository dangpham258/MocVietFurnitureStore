package mocviet.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOTPRequest {
    
    @NotBlank(message = "Mã OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã OTP phải có đúng 6 chữ số")
    private String otpCode;
    
    @NotBlank(message = "Email không được để trống")
    private String email;
}
