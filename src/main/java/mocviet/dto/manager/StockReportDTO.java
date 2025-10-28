package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho báo cáo tồn kho chi tiết
 * UC-MGR-INV-ViewStockReport
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDTO {
    
    private Integer variantId;
    private Integer productId;
    private String productName;
    private String categoryName;
    private String sku;
    private String colorName;
    private String colorHex;
    private String typeName;
    private Integer stockQty;
    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal stockValue; // stock_qty × sale_price
    private Boolean isActive;
    private LocalDateTime updatedAt;
    
    /**
     * Trạng thái tồn kho
     */
    public String getStockStatus() {
        if (stockQty == 0) {
            return "Hết hàng";
        } else if (stockQty <= 5) {
            return "Tồn kho thấp";
        } else if (stockQty <= 20) {
            return "Tồn kho vừa";
        }
        return "Tồn kho tốt";
    }
    
    /**
     * Class CSS cho trạng thái
     */
    public String getStockStatusClass() {
        if (stockQty == 0) {
            return "text-danger fw-bold";
        } else if (stockQty <= 5) {
            return "text-warning fw-bold";
        } else if (stockQty <= 20) {
            return "text-info";
        }
        return "text-success";
    }
}

