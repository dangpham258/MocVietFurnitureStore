package mocviet.dto.admin;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponUpdateRequest {
    
    @NotNull(message = "% giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "% giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100.00", message = "% giảm giá không được vượt quá 100%")
    private BigDecimal discountPercent;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
    
    private Boolean active;
    
    @NotNull(message = "Giá trị đơn hàng tối thiểu không được để trống")
    @DecimalMin(value = "0.00", message = "Giá trị đơn hàng tối thiểu không được âm")
    private BigDecimal minOrderAmount;
}

