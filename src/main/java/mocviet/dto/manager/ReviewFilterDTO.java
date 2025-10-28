package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho filter/search đánh giá
 * Sử dụng cho UC-MGR-REV-ViewAllReviews
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilterDTO {
    private String keyword;           // Tìm kiếm theo tên sản phẩm/khách hàng
    private Integer productId;        // Lọc theo sản phẩm
    private Integer rating;           // Lọc theo số sao (1-5)
    private Boolean isHidden;         // Lọc theo trạng thái (null = tất cả)
    private Boolean unanswered;       // Lọc đánh giá chưa trả lời
    private String sortBy;            // Sắp xếp: createdAt, rating
    private String sortDir;           // asc, desc
}

