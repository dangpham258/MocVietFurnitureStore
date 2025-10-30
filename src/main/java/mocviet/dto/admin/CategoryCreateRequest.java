package mocviet.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotNull(message = "Loại danh mục không được để trống")
    private String type;

    private Integer parentId;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 120, message = "Tên danh mục phải từ 2 đến 120 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(min = 2, max = 160, message = "Slug phải từ 2 đến 160 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    private Boolean isActive = true;
}

