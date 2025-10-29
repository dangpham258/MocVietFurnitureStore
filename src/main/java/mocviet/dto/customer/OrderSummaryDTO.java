package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Integer id;
    private String status;
    private String returnStatus;
    private String paymentStatus;
    private String paymentMethod;
    private BigDecimal shippingFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal subtotal; // optional, may be null if not computed
    private java.util.List<OrderItemDTO> items;
}


