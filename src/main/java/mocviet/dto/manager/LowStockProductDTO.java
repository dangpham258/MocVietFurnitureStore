package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho sản phẩm tồn kho thấp
 * UC-MGR-INV-ManageLowStock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockProductDTO {
    
    private Integer variantId;
    private Integer productId;
    private String productName;
    private String productSlug;
    private String sku;
    private String colorName;
    private String colorHex;
    private String typeName;
    private Integer stockQty;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Boolean isActive;
    private LocalDateTime updatedAt;
    
    // Thống kê bán hàng (để dự báo)
    private Integer soldLastMonth;      // Số lượng đã bán trong tháng qua
    private Double avgDailySales;       // Trung bình bán/ngày
    private Integer estimatedDaysLeft;  // Dự báo số ngày còn lại trước khi hết hàng
    
    /**
     * Tính dự báo hết hàng
     */
    public String getForecast() {
        if (stockQty == 0) {
            return "Đã hết hàng";
        }
        
        if (avgDailySales == null || avgDailySales == 0) {
            return "Chưa có dữ liệu bán";
        }
        
        estimatedDaysLeft = (int) Math.ceil(stockQty / avgDailySales);
        
        if (estimatedDaysLeft <= 3) {
            return "Dự kiến hết trong " + estimatedDaysLeft + " ngày";
        } else if (estimatedDaysLeft <= 7) {
            return "Dự kiến hết trong " + estimatedDaysLeft + " ngày";
        } else {
            return "Tồn kho ổn định";
        }
    }
    
    /**
     * Class CSS cho dự báo
     */
    public String getForecastClass() {
        if (stockQty == 0) {
            return "text-danger fw-bold";
        }
        
        if (estimatedDaysLeft != null) {
            if (estimatedDaysLeft <= 3) {
                return "text-danger fw-bold";
            } else if (estimatedDaysLeft <= 7) {
                return "text-warning";
            }
        }
        
        return "text-muted";
    }
    
    /**
     * Mức độ ưu tiên xử lý
     */
    public int getActionPriority() {
        if (stockQty == 0) {
            return 1; // Cao nhất
        }
        
        if (estimatedDaysLeft != null) {
            if (estimatedDaysLeft <= 3) {
                return 2;
            } else if (estimatedDaysLeft <= 7) {
                return 3;
            }
        }
        
        return 10; // Thấp nhất
    }
}

