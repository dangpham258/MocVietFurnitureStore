package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productSlug;
    private BigDecimal price;
    private String imageUrl;
    private Boolean inStock;
}


