package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListDTO {
    private Integer id;
    private String title;
    private String slug;
    private String articleType; // MEDIA, NEWS, PEOPLE
    private String summary;
    private String thumbnail;
    private String author;
    private Integer views;
    private Boolean isFeatured;
    private Boolean status; // false = nháp, true = hiển thị
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private Integer linkedProductId;
    private String linkedProductName; // Tên sản phẩm liên quan
}

