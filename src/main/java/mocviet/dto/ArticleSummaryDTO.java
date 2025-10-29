package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Article; // Import Article entity

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSummaryDTO {
    private Integer id;
    private String title;
    private String slug;
    private String summary;
    private String thumbnail;
    private Article.ArticleType articleType; // Sử dụng enum từ Entity
    private LocalDateTime publishedAt;

    // Hàm chuyển đổi từ Entity sang DTO
    public static ArticleSummaryDTO fromEntity(Article article) {
        if (article == null) {
            return null;
        }
        return new ArticleSummaryDTO(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getThumbnail(),
                article.getArticleType(),
                article.getPublishedAt()
        );
    }
}