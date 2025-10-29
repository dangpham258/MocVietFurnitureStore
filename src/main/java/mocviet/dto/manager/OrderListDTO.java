package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {
    private Integer id;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String city;
    private Orders.OrderStatus status;
    private Orders.PaymentMethod paymentMethod;
    private Orders.PaymentStatus paymentStatus;
    private Orders.ReturnStatus returnStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String deliveryTeamName;
    private String deliveryStatus;
    private Integer itemCount;
    private String statusDisplay;
    private String paymentStatusDisplay;
    private String returnStatusDisplay;
    private String deliveryStatusDisplay;
}
