package mocviet.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CouponCreateRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(max = 50, message = "Mã giảm giá không được quá 50 ký tự")
    private String code;

    @NotNull(message = "% giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "% giảm giá phải lớn hơn 0")
    @DecimalMax(value = "100.00", message = "% giảm giá không được vượt quá 100%")
    private BigDecimal discountPercent;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    private Boolean active = true;

    @NotNull(message = "Giá trị đơn hàng tối thiểu không được để trống")
    @DecimalMin(value = "0.00", message = "Giá trị đơn hàng tối thiểu không được âm")
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
}

