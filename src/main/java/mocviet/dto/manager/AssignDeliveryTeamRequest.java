package mocviet.dto.manager;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignDeliveryTeamRequest {
    
    @NotNull(message = "Order ID không được để trống")
    private Integer orderId;
    
    @NotNull(message = "Delivery Team ID không được để trống")
    private Integer deliveryTeamId;
    
    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;
    
    @Size(max = 20, message = "Số điện thoại liên hệ không được vượt quá 20 ký tự")
    private String contactPhone;
    
    @Size(max = 500, message = "Thời gian giao hàng mong muốn không được vượt quá 500 ký tự")
    private String preferredDeliveryTime;
}
