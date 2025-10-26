package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ColorCreateRequest {
    
    @NotBlank(message = "Tên màu không được để trống")
    @Size(min = 2, max = 80, message = "Tên màu phải từ 2 đến 80 ký tự")
    private String name;
    
    @NotBlank(message = "Slug không được để trống")
    @Size(min = 2, max = 100, message = "Slug phải từ 2 đến 100 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ chứa chữ thường, số và dấu gạch ngang")
    private String slug;
    
    @Pattern(regexp = "^$|^#[0-9A-Fa-f]{6}$", message = "Mã màu HEX không hợp lệ")
    @Size(max = 7, message = "Mã màu HEX không được quá 7 ký tự")
    private String hex;
    
    private Boolean isActive = true;
}

