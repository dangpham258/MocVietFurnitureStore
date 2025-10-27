package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaticPageCreateRequest {
    
    @NotBlank(message = "Slug không được để trống")
    @Size(max = 120, message = "Slug không được quá 120 ký tự")
    private String slug;
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được quá 200 ký tự")
    private String title;
    
    private String content;
    
    private Boolean isActive = true;
}

