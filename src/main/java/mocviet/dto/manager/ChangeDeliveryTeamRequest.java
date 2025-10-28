package mocviet.dto.manager;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDeliveryTeamRequest {
    
    @NotNull(message = "Order ID không được để trống")
    private Integer orderId;
    
    @NotNull(message = "Delivery Team ID mới không được để trống")
    private Integer newDeliveryTeamId;
    
    @NotBlank(message = "Lý do thay đổi không được để trống")
    @Size(max = 500, message = "Lý do thay đổi không được vượt quá 500 ký tự")
    private String reason;
    
    @Size(max = 500, message = "Ghi chú bổ sung không được vượt quá 500 ký tự")
    private String note;
}
