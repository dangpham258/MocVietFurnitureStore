package mocviet.dto.manager;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request trả lời đánh giá
 * Sử dụng cho UC-MGR-REV-RespondToReview
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseRequest {
    
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    @Size(max = 1000, message = "Nội dung phản hồi tối đa 1000 ký tự")
    private String response;
}

