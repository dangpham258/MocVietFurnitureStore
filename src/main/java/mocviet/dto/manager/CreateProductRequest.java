package mocviet.dto.manager;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 160, message = "Tên sản phẩm phải có từ 2-160 ký tự")
    private String name;
    
    @Size(max = 2000, message = "Mô tả sản phẩm không được quá 2000 ký tự")
    private String description;
    
    @NotNull(message = "Vui lòng chọn danh mục")
    private Integer categoryId;
    
    private Integer collectionId;
    
    @NotNull(message = "Vui lòng chọn màu sắc")
    private Integer colorId;
    
    @NotBlank(message = "Loại/kích thước không được để trống")
    @Size(max = 80, message = "Loại/kích thước không được quá 80 ký tự")
    private String typeName;
    
    @NotBlank(message = "SKU không được để trống")
    @Size(min = 3, max = 80, message = "SKU phải có từ 3-80 ký tự")
    @Pattern(regexp = "^[A-Z0-9-_]+$", message = "SKU chỉ được chứa chữ hoa, số, gạch ngang và gạch dưới")
    private String sku;
    
    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0", message = "Giá sản phẩm phải >= 0")
    @Digits(integer = 15, fraction = 0, message = "Giá sản phẩm không hợp lệ")
    private BigDecimal price;
    
    @Min(value = 0, message = "% giảm giá phải >= 0")
    @Max(value = 100, message = "% giảm giá phải <= 100")
    private Integer discountPercent = 0;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer stockQty = 0;
    
    @Pattern(regexp = "^(SALE|OUTLET)$", message = "Loại khuyến mãi phải là SALE hoặc OUTLET")
    private String promotionType;
    
    private List<String> imageUrls;
}
