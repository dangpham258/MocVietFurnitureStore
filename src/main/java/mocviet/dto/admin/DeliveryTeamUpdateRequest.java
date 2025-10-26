package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryTeamUpdateRequest {
    
    @NotBlank(message = "Tên đội không được để trống")
    private String name;
    
    private String phone;
    
    @NotNull(message = "User ID không được để trống")
    private Integer userId;
    
    private Boolean isActive;
}

