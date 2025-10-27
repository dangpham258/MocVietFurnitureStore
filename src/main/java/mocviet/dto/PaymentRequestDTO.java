package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho request thanh toán qua VNPAY/MoMo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Integer orderId;
    private String paymentMethod; // VNPAY hoặc MOMO
    private BigDecimal amount;
    private String orderDescription; // Mô tả đơn hàng
    private String returnUrl; // URL để payment gateway redirect về sau khi thanh toán
    private String ipAddress; // IP của người dùng
}
