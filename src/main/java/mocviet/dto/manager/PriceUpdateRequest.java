package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceUpdateRequest {
    
    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0", message = "Giá sản phẩm phải >= 0")
    @Digits(integer = 15, fraction = 0, message = "Giá sản phẩm không hợp lệ")
    private BigDecimal price;
    
    @Min(value = 0, message = "% giảm giá phải >= 0")
    @Max(value = 100, message = "% giảm giá phải <= 100")
    private Integer discountPercent = 0;
    
    @Pattern(regexp = "^(SALE|OUTLET)$", message = "Loại khuyến mãi phải là SALE hoặc OUTLET")
    private String promotionType;
}
