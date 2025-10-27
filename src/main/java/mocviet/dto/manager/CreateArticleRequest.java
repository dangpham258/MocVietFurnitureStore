package mocviet.dto.manager;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 300, message = "Tiêu đề không được vượt quá 300 ký tự")
    private String title;
    
    @NotNull(message = "Loại bài viết không được để trống")
    private String articleType; // MEDIA, NEWS, PEOPLE
    
    @Size(max = 500, message = "Tóm tắt không được vượt quá 500 ký tự")
    private String summary;
    
    private String content;
    
    private Integer linkedProductId; // Sản phẩm liên quan (tùy chọn)
    
    private Boolean isFeatured = false; // Bài viết nổi bật
    
    private Boolean status = false; // false = nháp, true = xuất bản
}

