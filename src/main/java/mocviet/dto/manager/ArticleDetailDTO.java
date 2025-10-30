package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailDTO {
    private Integer id;
    private String title;
    private String slug;
    private String articleType; // MEDIA, NEWS, PEOPLE
    private String summary;
    private String content;
    private String thumbnail;
    private String author;
    private Integer views;
    private Boolean isFeatured;
    private Boolean status;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private Integer linkedProductId;
    private String linkedProductName;
    private String linkedProductSlug;
    private List<ArticleImageDTO> images;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleImageDTO {
        private Integer id;
        private String url;
        private String caption;
    }
}

