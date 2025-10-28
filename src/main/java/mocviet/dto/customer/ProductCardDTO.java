package mocviet.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductCardDTO {
    
    private Integer id;
    private String name;
    private String slug;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private int discountPercent;
    private double avgRating; // [cite: 55]
    private int totalReviews;
    private String promotionType; // [cite: 60, 343]
}