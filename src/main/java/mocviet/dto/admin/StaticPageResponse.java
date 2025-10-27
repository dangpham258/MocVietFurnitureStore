package mocviet.dto.admin;

import lombok.Data;

@Data
public class StaticPageResponse {
    
    private Integer id;
    
    private String slug;
    
    private String title;
    
    private String content;
    
    private Boolean isActive;
    
    private String updatedAt;
}

