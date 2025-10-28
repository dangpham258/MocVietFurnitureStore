package mocviet.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho tóm tắt tổng quan tồn kho
 * Dùng cho dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryDTO {
    
    private Long totalProducts;           // Tổng số sản phẩm (variants)
    private Long outOfStockProducts;      // Số sản phẩm hết hàng (stock_qty = 0)
    private Long lowStockProducts;        // Số sản phẩm tồn kho thấp (1-5)
    private Long inStockProducts;         // Số sản phẩm có tồn kho (> 0)
    private BigDecimal totalStockValue;   // Tổng giá trị tồn kho
    
    /**
     * Tính phần trăm sản phẩm hết hàng
     */
    public double getOutOfStockPercentage() {
        if (totalProducts == 0) return 0.0;
        return (outOfStockProducts * 100.0) / totalProducts;
    }
    
    /**
     * Tính phần trăm sản phẩm tồn kho thấp
     */
    public double getLowStockPercentage() {
        if (totalProducts == 0) return 0.0;
        return (lowStockProducts * 100.0) / totalProducts;
    }
    
    /**
     * Tính phần trăm sản phẩm có tồn kho
     */
    public double getInStockPercentage() {
        if (totalProducts == 0) return 0.0;
        return (inStockProducts * 100.0) / totalProducts;
    }
}

