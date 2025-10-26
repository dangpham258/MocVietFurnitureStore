package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Orders;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {
    private Integer orderId;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String city;
    private Orders.ReturnStatus returnStatus;
    private String returnReason;
    private String returnNote;
    private LocalDateTime orderDeliveredAt;
    private LocalDateTime returnRequestedAt;
    private long daysSinceDelivery;
    private boolean isWithinReturnPeriod;
    private List<OrderItemDTO> orderItems;
    private String deliveryTeamName;
    private String deliveryTeamPhone;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Integer id;
        private String productName;
        private String variantSku;
        private String colorName;
        private String typeName;
        private Integer quantity;
        private String productImageUrl;
    }
}
