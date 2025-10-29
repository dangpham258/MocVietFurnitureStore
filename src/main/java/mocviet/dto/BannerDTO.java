package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Banner hiển thị trên trang chủ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Integer id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private LocalDateTime createdAt;
    
    /**
     * Chuyển đổi từ Banner entity sang BannerDTO
     */
    public static BannerDTO fromEntity(mocviet.entity.Banner banner) {
        if (banner == null) {
            return null;
        }
        return BannerDTO.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}

