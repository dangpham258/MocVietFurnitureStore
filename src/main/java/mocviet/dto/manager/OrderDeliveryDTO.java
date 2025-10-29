package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryDTO {
    
    private Integer id;
    private Integer orderId;
    private Integer deliveryTeamId;
    private String deliveryTeamName;
    private String status;
    private String proofImageUrl;
    private String note;
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String zoneName;
    private Integer orderTotal;
    private String orderStatus;
}
