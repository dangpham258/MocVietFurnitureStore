package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho cảnh báo tồn kho
 * UC-MGR-INV-ViewStockAlerts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertDTO {
    
    private Integer variantId;
    private Integer productId;
    private String productName;
    private String sku;
    private String colorName;
    private String colorHex;
    private String typeName;
    private Integer stockQty;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String alertLevel; // "OUT_OF_STOCK" hoặc "LOW_STOCK"
    private LocalDateTime updatedAt;
    private Boolean isActive;
    
    /**
     * Xác định mức độ ưu tiên cảnh báo
     * OUT_OF_STOCK (0) > LOW_STOCK (1-5)
     */
    public int getPriority() {
        if (stockQty == 0) {
            return 1; // Ưu tiên cao nhất
        } else if (stockQty <= 5) {
            return 2 - stockQty; // Càng ít càng ưu tiên
        }
        return 10;
    }
    
    /**
     * Trả về trạng thái hiển thị
     */
    public String getStatusDisplay() {
        if (stockQty == 0) {
            return "Hết hàng";
        } else if (stockQty <= 5) {
            return "Tồn kho thấp";
        }
        return "Bình thường";
    }
    
    /**
     * Trả về class CSS cho badge
     */
    public String getStatusBadgeClass() {
        if (stockQty == 0) {
            return "badge bg-danger";
        } else if (stockQty <= 5) {
            return "badge bg-warning";
        }
        return "badge bg-success";
    }
}

