package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingOrderDTO {
    
    private Integer id;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String city;
    private String district;
    private String zoneName;
    private BigDecimal orderTotal;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String paymentStatus;
    private Integer itemCount;
    private String orderStatus;
}
