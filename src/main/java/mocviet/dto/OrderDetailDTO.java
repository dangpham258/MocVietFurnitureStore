package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private Integer id;
    private String status;
    private String returnStatus;
    private String returnReason;
    private String returnNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal shippingFee;
    private String paymentMethod;
    private String paymentStatus;
    
    // Address info
    private String receiverName;
    private String phone;
    private String addressLine;
    private String district;
    private String city;
    
    // Coupon info
    private String couponCode;
    
    // Order items
    private List<OrderItemDTO> items;
    
    // Status histories
    private List<StatusHistoryDTO> statusHistories;
    
    // Calculated totals
    private BigDecimal subtotal;
    private BigDecimal total;
    
    // Action flags
    private boolean canCancel;
    private boolean canReview;
    private boolean canRequestReturn;
    private boolean canReorder;
}
