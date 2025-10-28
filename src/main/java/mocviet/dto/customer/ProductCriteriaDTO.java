package mocviet.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

// DTO chứa các tiêu chí lọc/tìm kiếm từ Guest
@Data
@NoArgsConstructor
public class ProductCriteriaDTO {
    
    // Tìm kiếm theo tên
    private String name;
    
    // Lọc theo danh mục
    private String categorySlug;
    
    // Lọc theo bộ sưu tập
    private String collectionSlug;
    
    // Lọc theo màu
    private Integer colorId;
    
    // Lọc theo khoảng giá
    private Double minPrice;
    private Double maxPrice;
    
    // Sắp xếp [cite: 51]
    // Ví dụ: "newest", "price_asc", "price_desc", "best_selling"
    private String sortBy = "newest"; 
    
    // Phân trang
    private int page = 0;
    private int size = 20; // [cite: 25, 338]
}