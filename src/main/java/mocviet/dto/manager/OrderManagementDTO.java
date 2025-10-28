package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderManagementDTO {
    private Integer id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String deliveryAddress;
    private String city;
    private String district;
    private Orders.OrderStatus status;
    private Orders.PaymentMethod paymentMethod;
    private Orders.PaymentStatus paymentStatus;
    private Orders.ReturnStatus returnStatus;
    private String returnReason;
    private String returnNote;
    private String couponCode;
    private BigDecimal shippingFee;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItems;
    private List<OrderStatusHistoryDTO> statusHistories;
    private String deliveryTeamName;
    private String deliveryTeamPhone;
    private String deliveryStatus;
    private String deliveryNote;
    private String proofImageUrl;
    private List<DeliveryHistoryDTO> deliveryHistories;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Integer id;
        private String productName;
        private String productSlug;
        private String variantSku;
        private String colorName;
        private String typeName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String productImageUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusHistoryDTO {
        private Integer id;
        private Orders.OrderStatus status;
        private String note;
        private String changedBy;
        private LocalDateTime changedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryHistoryDTO {
        private Integer id;
        private String status;
        private String note;
        private String photoUrl;
        private LocalDateTime changedAt;
    }
}
