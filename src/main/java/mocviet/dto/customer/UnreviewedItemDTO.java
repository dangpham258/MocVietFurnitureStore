package mocviet.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreviewedItemDTO {
    private Integer id;
    private String sku;
    private String colorName;
    private String typeName;
    private Integer qty;
    private String productSlug;
    private String productName; // for display
}

