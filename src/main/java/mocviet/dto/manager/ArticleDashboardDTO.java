package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDashboardDTO {
    private long totalArticles;      // Tổng số bài viết
    private long publishedArticles;  // Số bài viết đã xuất bản
    private long draftArticles;      // Số bài viết nháp
    private Long totalViews;         // Tổng lượt xem
}

