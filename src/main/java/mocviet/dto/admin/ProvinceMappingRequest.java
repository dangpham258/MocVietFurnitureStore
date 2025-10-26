package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProvinceMappingRequest {
    
    @NotNull(message = "Zone ID không được để trống")
    private Integer zoneId;
    
    @NotBlank(message = "Tên tỉnh/thành không được để trống")
    private String provinceName;
}

