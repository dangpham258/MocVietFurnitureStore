package mocviet.service.customer;

import mocviet.dto.PaymentRequestDTO;
import mocviet.dto.PaymentWebhookDTO;
import mocviet.entity.Orders;

/**
 * Interface cho Payment Service
 */
public interface IPaymentService {
    
    /**
     * Tạo payment URL cho VNPAY hoặc MoMo
     * @param request Payment request DTO
     * @return Payment URL
     */
    String createPaymentUrl(PaymentRequestDTO request);
    
    /**
     * Verify và xử lý webhook từ payment gateway
     * @param webhookDto DTO từ webhook
     * @return true nếu verify thành công và đã xử lý
     */
    boolean handlePaymentWebhook(PaymentWebhookDTO webhookDto);
    
    /**
     * Verify checksum/hash của payment gateway
     * @param webhookDto DTO từ webhook
     * @return true nếu checksum hợp lệ
     */
    boolean verifyChecksum(PaymentWebhookDTO webhookDto);
}
