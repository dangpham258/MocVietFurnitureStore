package mocviet.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CouponResponse {
    private String code;
    private BigDecimal discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private BigDecimal minOrderAmount;
}

