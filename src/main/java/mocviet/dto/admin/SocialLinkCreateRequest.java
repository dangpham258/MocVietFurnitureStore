package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SocialLinkCreateRequest {
    
    @NotNull(message = "Platform không được để trống")
    private String platform;
    
    @NotBlank(message = "URL không được để trống")
    private String url;
    
    private Boolean isActive = true;
}

