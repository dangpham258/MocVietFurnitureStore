package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutSummaryDTO {
    private List<CheckoutItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private String couponCode;
    private String selectedPaymentMethod;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutItemDTO {
        private Integer cartItemId;
        private Integer variantId;
        private String productName;
        private String productSlug;
        private String colorName;
        private String typeName;
        private BigDecimal unitPrice;
        private Integer qty;
        private BigDecimal totalPrice;
        private String imageUrl;
    }
}

