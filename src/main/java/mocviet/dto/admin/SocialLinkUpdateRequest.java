package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLinkUpdateRequest {

    @NotBlank(message = "URL không được để trống")
    private String url;

    private Boolean isActive;
}

