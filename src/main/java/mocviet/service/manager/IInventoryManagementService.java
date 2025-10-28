package mocviet.service.manager;

import mocviet.dto.manager.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface IInventoryManagementService {
    StockSummaryDTO getStockSummary();
    Page<StockAlertDTO> getStockAlerts(Pageable pageable, String alertType, String keyword);
    void updateStock(UpdateStockRequest request, Integer managerId);
    StockAlertDTO getVariantDetails(Integer variantId);
    Page<StockReportDTO> getStockReport(Pageable pageable, Integer categoryId, String stockLevel, String keyword);
    Page<LowStockProductDTO> getLowStockProducts(Pageable pageable, String keyword);
    void hideProduct(Integer variantId, Integer managerId, String reason);
    void showProduct(Integer variantId, Integer managerId);
    Page<LowStockProductDTO> getHiddenProducts(Pageable pageable, String keyword, Integer categoryId, String stockLevel);
    Map<String, Object> getHiddenProductsSummary();
}


