package mocviet.dto.manager;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho cập nhật tồn kho
 * UC-MGR-INV-UpdateStock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {
    
    @NotNull(message = "Variant ID không được để trống")
    private Integer variantId;
    
    @NotNull(message = "Số lượng tồn kho mới không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer newStockQty;
    
    private String note; // Ghi chú lý do cập nhật (tùy chọn)
}

