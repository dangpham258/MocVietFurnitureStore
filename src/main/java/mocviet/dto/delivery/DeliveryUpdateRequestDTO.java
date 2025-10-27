package mocviet.dto.delivery;

import jakarta.validation.constraints.NotBlank; // Thêm validation nếu cần
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO nhận dữ liệu khi Delivery cập nhật trạng thái
@Data
public class DeliveryUpdateRequestDTO {

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;

    // Tạm thời là URL, sau này có thể đổi thành MultipartFile
    @Size(max = 255, message = "URL ảnh không được vượt quá 255 ký tự")
    private String proofImageUrl;

    // Bắt buộc khi thu hồi hàng (trạng thái RETURN_PICKUP)
    private String refundMethod; // COD_CASH, BANK_TRANSFER, VNPAY, MOMO
}