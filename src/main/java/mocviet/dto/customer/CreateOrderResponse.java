package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    private Integer orderId;
    private String status;
    private String paymentStatus;
    private BigDecimal subtotalSnapshot;
    private BigDecimal discountAmount;
    private BigDecimal totalAfterCoupon;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;
    private String paymentUrl; // URL thanh toán nếu là VNPAY/MoMo
    private String message;
}

