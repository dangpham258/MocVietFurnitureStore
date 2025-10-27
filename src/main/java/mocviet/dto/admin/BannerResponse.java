package mocviet.dto.admin;

import lombok.Data;

@Data
public class BannerResponse {
    private Integer id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Boolean isActive;
    private String createdAt;
}

