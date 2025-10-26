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
    private Boolean hasProducts; // Indicates if this category has associated products
    private Boolean hasChildren; // Indicates if this category has child categories
}

