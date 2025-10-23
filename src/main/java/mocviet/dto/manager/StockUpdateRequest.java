package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StockUpdateRequest {
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer stockQty;
    
    private String note;
}
