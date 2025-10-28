package mocviet.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;
import mocviet.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProductDetailDTO {
    
    private Integer id;
    private String name; // [cite: 54]
    private String slug;
    private String description; // [cite: 64]
    private double avgRating; // [cite: 55]
    private int totalReviews;
    private int soldQty; // [cite: 56]
    
    // DTO cho các biến thể (variants)
    private List<VariantDTO> variants;
    
    // DTO cho hình ảnh (grupp theo colorId)
    // Map<ColorID, List<ImageURL>>
    private Map<Integer, List<String>> imagesByColor; // [cite: 53, 69, 345]
    
    // DTO cho các lựa chọn (để render UI)
    private List<ColorOptionDTO> colorOptions; // [cite: 61]
    private List<TypeOptionDTO> typeOptions;   // [cite: 62]
    
    // Dùng để tạo DTO từ Entity
    public static ProductDetailDTO fromEntity(Product product) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setAvgRating(product.getAvgRating() != null ? product.getAvgRating().doubleValue() : 0.0);
        dto.setTotalReviews(product.getTotalReviews());
        dto.setSoldQty(product.getSoldQty());
        return dto;
    }
    
    // Nested DTOs
    
    @Data
    @NoArgsConstructor
    public static class VariantDTO {
        private Integer variantId;
        private String sku; // [cite: 66, 73]
        private Integer colorId;
        private String typeName;
        private BigDecimal price; // [cite: 59]
        private BigDecimal salePrice; // [cite: 58]
        private int discountPercent; // [cite: 57]
        private int stockQty; // [cite: 65, 70, 346]
        private String promotionType; // [cite: 60, 343]
    }
    
    @Data
    @NoArgsConstructor
    public static class ColorOptionDTO {
        private Integer colorId;
        private String name;
        private String hex;
    }
    
    @Data
    @NoArgsConstructor
    public static class TypeOptionDTO {
        private String typeName;
    }
}