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
public class CartItemDTO {
    private Integer id;
    private Integer variantId;
    private String sku;
    private String productName;
    private String productSlug;
    private String colorName;
    private String typeName;
    private BigDecimal unitPrice;
    private Integer qty;
    private BigDecimal totalPrice;
    private String imageUrl;
    private Integer stockQty; // số lượng tồn tối đa để set max cho input
}


