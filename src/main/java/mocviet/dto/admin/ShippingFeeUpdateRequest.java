package mocviet.dto.admin;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShippingFeeUpdateRequest {

    @NotNull(message = "Phí vận chuyển không được để trống")
    @DecimalMin(value = "0.00", message = "Phí vận chuyển không được âm")
    private BigDecimal baseFee;
}

