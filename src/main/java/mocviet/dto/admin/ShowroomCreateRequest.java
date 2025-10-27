package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShowroomCreateRequest {
    
    @NotBlank(message = "Tên showroom không được để trống")
    @Size(max = 120, message = "Tên showroom không được vượt quá 120 ký tự")
    private String name;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;
    
    @NotBlank(message = "Tỉnh/Thành không được để trống")
    @Size(max = 100, message = "Tỉnh/Thành không được vượt quá 100 ký tự")
    private String city;
    
    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;
    
    @Size(min = 9, max = 20, message = "Số điện thoại phải có từ 9 đến 20 ký tự")
    @Pattern(regexp = "^[0-9+\\-\\(\\)\\s]*$", message = "Số điện thoại chỉ được chứa số, dấu +, -, (), và khoảng trắng")
    private String phone;
    
    @Email(message = "Email không hợp lệ")
    @Size(max = 120, message = "Email không được vượt quá 120 ký tự")
    private String email;
    
    @Size(max = 120, message = "Giờ mở cửa không được vượt quá 120 ký tự")
    private String openHours;
    
    private String mapEmbed;
    
    private Boolean isActive = true;
}

