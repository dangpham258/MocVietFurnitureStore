package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderActionRequest {
    
    @NotNull(message = "Mã đơn hàng không được để trống")
    private Integer orderId;
    
    private String note;
    
    private String reason;
    
    private Integer deliveryTeamId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmOrderRequest {
        @NotNull(message = "Mã đơn hàng không được để trống")
        private Integer orderId;
        
        private String note;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelOrderRequest {
        @NotNull(message = "Mã đơn hàng không được để trống")
        private Integer orderId;
        
        @NotBlank(message = "Lý do hủy đơn không được để trống")
        private String reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproveReturnRequest {
        @NotNull(message = "Mã đơn hàng không được để trống")
        private Integer orderId;
        
        private String note;
        
        private Integer deliveryTeamId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectReturnRequest {
        @NotNull(message = "Mã đơn hàng không được để trống")
        private Integer orderId;
        
        @NotBlank(message = "Lý do từ chối không được để trống")
        private String reason;
    }
}
