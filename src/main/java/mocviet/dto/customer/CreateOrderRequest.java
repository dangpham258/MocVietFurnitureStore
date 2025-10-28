package mocviet.dto.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Địa chỉ giao hàng là bắt buộc")
    private Integer addressId;
    
    private String couponCode;
    
    @NotNull(message = "Phương thức thanh toán là bắt buộc")
    private String paymentMethod; // COD, VNPAY, MOMO
    
    @NotEmpty(message = "Giỏ hàng không được trống")
    private List<OrderItemRequest> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Variant ID là bắt buộc")
        private Integer variantId;
        
        @NotNull(message = "Số lượng là bắt buộc")
        private Integer qty;
    }
}

