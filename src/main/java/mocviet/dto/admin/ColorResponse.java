package mocviet.dto.admin;

import lombok.Data;

@Data
public class ColorResponse {
    private Integer id;
    private String name;
    private String slug;
    private String hex;
    private Boolean isActive;
    private Boolean hasImages; // Indicates if this color has associated product images
}

