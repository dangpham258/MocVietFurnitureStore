package mocviet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;
    private String sku;
    private String colorName;
    private String typeName;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String productSlug;
}
