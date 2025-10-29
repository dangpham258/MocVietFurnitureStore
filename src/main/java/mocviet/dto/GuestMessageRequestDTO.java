package mocviet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // <<<--- ĐÃ THÊM

@Data // <<<--- ĐÃ THÊM
public class GuestMessageRequestDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 120, message = "Họ tên không được vượt quá 120 ký tự")
    private String guestName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 120, message = "Email không được vượt quá 120 ký tự")
    private String guestEmail;

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 2000, message = "Nội dung không được vượt quá 2000 ký tự")
    private String content;

    // private MultipartFile attachment;
}