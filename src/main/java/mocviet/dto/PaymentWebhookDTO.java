package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho webhook từ VNPAY/MoMo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookDTO {
    private String transactionCode; // Mã giao dịch từ gateway
    private String orderId; // Order ID trong hệ thống của chúng ta
    private String amount; // Số tiền
    private String paymentStatus; // Trạng thái thanh toán (00 = success, các mã khác = fail)
    private String paymentGateway; // VNPAY hoặc MOMO
    private String checksum; // Để verify tính toàn vẹn của dữ liệu
}
