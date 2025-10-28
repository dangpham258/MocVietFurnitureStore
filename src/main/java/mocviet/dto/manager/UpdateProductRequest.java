package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 160, message = "Tên sản phẩm phải có từ 2-160 ký tự")
    private String name;
    
    @Size(max = 2000, message = "Mô tả sản phẩm không được quá 2000 ký tự")
    private String description;
    
    @NotNull(message = "Vui lòng chọn danh mục")
    private Integer categoryId;
    
    private Integer collectionId;
}
