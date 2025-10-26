package mocviet.dto.admin;

import lombok.Data;

@Data
public class SocialLinkResponse {
    private Integer id;
    private String platform;
    private String url;
    private Boolean isActive;
}

