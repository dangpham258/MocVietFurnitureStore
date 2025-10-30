package mocviet.dto.admin;

import lombok.Data;

@Data
public class CategoryResponse {
    private Integer id;
    private Integer parentId;
    private String parentName;
    private String name;
    private String slug;
    private String type;
    private Boolean isActive;
    private Boolean hasProducts; // Xác định xem danh mục có sản phẩm hay không
    private Boolean hasChildren; // Xác định xem danh mục có danh mục con hay không
}

