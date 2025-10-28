package mocviet.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ZoneMappingRequest {

    @NotNull(message = "Zone ID không được để trống")
    private Integer zoneId;
}

