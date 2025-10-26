package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.ProductVariant;
import mocviet.repository.ProductVariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Service quản lý tồn kho cho Manager
 * Implements các use case:
 * - UC-MGR-INV-ViewStockAlerts: Xem cảnh báo tồn kho
 * - UC-MGR-INV-UpdateStock: Cập nhật số lượng tồn kho
 * - UC-MGR-INV-ViewStockReport: Xem báo cáo tồn kho
 * - UC-MGR-INV-ManageLowStock: Quản lý sản phẩm tồn kho thấp
 */
@Service
@RequiredArgsConstructor
public class InventoryManagementService {
    
    private final ProductVariantRepository productVariantRepository;
    private final JdbcTemplate jdbcTemplate;
    
    // ===== UC-MGR-INV-ViewStockAlerts: XEM CẢNH BÁO TỒN KHO =====
    
    /**
     * Lấy tổng quan tồn kho cho dashboard
     */
    @Transactional(readOnly = true)
    public StockSummaryDTO getStockSummary() {
        List<ProductVariant> allVariants = productVariantRepository.findAll();
        
        long totalProducts = allVariants.size();
        long outOfStock = allVariants.stream()
            .filter(v -> v.getStockQty() == 0 && v.getIsActive())
            .count();
        long lowStock = allVariants.stream()
            .filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5 && v.getIsActive())
            .count();
        long inStock = allVariants.stream()
            .filter(v -> v.getStockQty() > 0 && v.getIsActive())
            .count();
        
        BigDecimal totalStockValue = allVariants.stream()
            .filter(v -> v.getIsActive() && v.getSalePrice() != null)
            .map(v -> v.getSalePrice().multiply(BigDecimal.valueOf(v.getStockQty())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return StockSummaryDTO.builder()
            .totalProducts(totalProducts)
            .outOfStockProducts(outOfStock)
            .lowStockProducts(lowStock)
            .inStockProducts(inStock)
            .totalStockValue(totalStockValue)
            .build();
    }
    
    /**
     * Lấy danh sách cảnh báo tồn kho (hết hàng và tồn kho thấp)
     * Sắp xếp theo mức độ ưu tiên: hết hàng trước, sau đó tồn kho thấp
     */
    @Transactional(readOnly = true)
    public Page<StockAlertDTO> getStockAlerts(Pageable pageable, String alertType, String keyword) {
        List<ProductVariant> variants;
        
        // Lọc theo alertType
        if ("OUT_OF_STOCK".equals(alertType)) {
            variants = productVariantRepository.findAll().stream()
                .filter(v -> v.getStockQty() == 0 && v.getIsActive())
                .collect(Collectors.toList());
        } else if ("LOW_STOCK".equals(alertType)) {
            variants = productVariantRepository.findAll().stream()
                .filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5 && v.getIsActive())
                .collect(Collectors.toList());
        } else {
            // Tất cả cảnh báo (stock_qty <= 5 và active)
            variants = productVariantRepository.findAll().stream()
                .filter(v -> v.getStockQty() <= 5 && v.getIsActive())
                .collect(Collectors.toList());
        }
        
        // Lọc theo keyword (tên sản phẩm, SKU, màu sắc, loại sản phẩm)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream()
                .filter(v -> (v.getProduct().getName() != null && v.getProduct().getName().toLowerCase().contains(keywordLower)) ||
                            (v.getSku() != null && v.getSku().toLowerCase().contains(keywordLower)) ||
                            (v.getColor() != null && v.getColor().getName() != null && v.getColor().getName().toLowerCase().contains(keywordLower)) ||
                            (v.getTypeName() != null && v.getTypeName().toLowerCase().contains(keywordLower)))
                .collect(Collectors.toList());
        }
        
        // Convert sang DTO và sort theo priority
        List<StockAlertDTO> alerts = variants.stream()
            .map(this::mapToStockAlertDTO)
            .sorted(Comparator.comparingInt(StockAlertDTO::getPriority))
            .collect(Collectors.toList());
        
        // Phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), alerts.size());
        
        // Kiểm tra start có hợp lệ không
        if (start >= alerts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), alerts.size());
        }
        
        List<StockAlertDTO> pageContent = alerts.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, alerts.size());
    }
    
    /**
     * Map ProductVariant sang StockAlertDTO
     */
    private StockAlertDTO mapToStockAlertDTO(ProductVariant variant) {
        String alertLevel = variant.getStockQty() == 0 ? "OUT_OF_STOCK" : "LOW_STOCK";
        
        return StockAlertDTO.builder()
            .variantId(variant.getId())
            .productId(variant.getProduct().getId())
            .productName(variant.getProduct().getName())
            .sku(variant.getSku())
            .colorName(variant.getColor().getName())
            .colorHex(variant.getColor().getHex())
            .typeName(variant.getTypeName())
            .stockQty(variant.getStockQty())
            .price(variant.getPrice())
            .salePrice(variant.getSalePrice())
            .alertLevel(alertLevel)
            .updatedAt(LocalDateTime.now()) // Lấy từ variant nếu có tracking
            .isActive(variant.getIsActive())
            .build();
    }
    
    // ===== UC-MGR-INV-UpdateStock: CẬP NHẬT TỒN KHO =====
    
    /**
     * Cập nhật số lượng tồn kho cho một variant
     * Trigger TR_ProductVariant_StockAlerts sẽ tự động tạo thông báo
     */
    @Transactional
    public void updateStock(UpdateStockRequest request, Integer managerId) {
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + request.getVariantId()));
        
        // Validate
        if (!variant.getIsActive()) {
            throw new RuntimeException("Không thể cập nhật tồn kho cho sản phẩm đã vô hiệu hóa");
        }
        
        if (request.getNewStockQty() < 0) {
            throw new RuntimeException("Số lượng tồn kho phải >= 0");
        }
        
        // Lưu số lượng cũ để log
        Integer oldStockQty = variant.getStockQty();
        
        // Cập nhật tồn kho
        variant.setStockQty(request.getNewStockQty());
        productVariantRepository.save(variant);
        
        // Ghi log (có thể lưu vào bảng riêng nếu cần audit trail chi tiết)
        String logMessage = String.format(
            "Manager ID %d cập nhật tồn kho SKU %s từ %d -> %d. Ghi chú: %s",
            managerId, variant.getSku(), oldStockQty, request.getNewStockQty(),
            request.getNote() != null ? request.getNote() : "Không có ghi chú"
        );
        
        System.out.println("[INVENTORY LOG] " + logMessage);
        
        // Trigger TR_ProductVariant_StockAlerts sẽ tự động tạo thông báo
        // nếu tồn kho thấp (1-5) hoặc hết hàng (0) hoặc có hàng trở lại
    }
    
    /**
     * Lấy thông tin chi tiết variant để hiển thị form cập nhật
     */
    @Transactional(readOnly = true)
    public StockAlertDTO getVariantDetails(Integer variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        
        return mapToStockAlertDTO(variant);
    }
    
    // ===== UC-MGR-INV-ViewStockReport: XEM BÁO CÁO TỒN KHO =====
    
    /**
     * Lấy báo cáo tồn kho chi tiết với filter và search
     */
    @Transactional(readOnly = true)
    public Page<StockReportDTO> getStockReport(Pageable pageable, Integer categoryId, String stockLevel, String keyword) {
        List<ProductVariant> variants = productVariantRepository.findAll();
        
        // Lọc theo danh mục
        if (categoryId != null) {
            variants = variants.stream()
                .filter(v -> v.getProduct().getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
        }
        
        // Lọc theo mức tồn kho
        if (stockLevel != null && !stockLevel.isEmpty()) {
            switch (stockLevel) {
                case "OUT_OF_STOCK":
                    variants = variants.stream()
                        .filter(v -> v.getStockQty() == 0)
                        .collect(Collectors.toList());
                    break;
                case "LOW_STOCK":
                    variants = variants.stream()
                        .filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5)
                        .collect(Collectors.toList());
                    break;
                case "MEDIUM_STOCK":
                    variants = variants.stream()
                        .filter(v -> v.getStockQty() > 5 && v.getStockQty() <= 20)
                        .collect(Collectors.toList());
                    break;
                case "GOOD_STOCK":
                    variants = variants.stream()
                        .filter(v -> v.getStockQty() > 20)
                        .collect(Collectors.toList());
                    break;
            }
        }
        
        // Tìm kiếm theo keyword (tên sản phẩm, SKU, màu sắc, loại sản phẩm)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream()
                .filter(v -> v.getProduct().getName().toLowerCase().contains(keywordLower) ||
                            v.getSku().toLowerCase().contains(keywordLower) ||
                            v.getColor().getName().toLowerCase().contains(keywordLower) ||
                            v.getTypeName().toLowerCase().contains(keywordLower))
                .collect(Collectors.toList());
        }
        
        // Convert sang DTO
        List<StockReportDTO> reports = variants.stream()
            .map(this::mapToStockReportDTO)
            .collect(Collectors.toList());
        
        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), reports.size());
        
        // Kiểm tra start có hợp lệ không
        if (start >= reports.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), reports.size());
        }
        
        List<StockReportDTO> pageContent = reports.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, reports.size());
    }
    
    /**
     * Map ProductVariant sang StockReportDTO
     */
    private StockReportDTO mapToStockReportDTO(ProductVariant variant) {
        BigDecimal stockValue = BigDecimal.ZERO;
        if (variant.getSalePrice() != null) {
            stockValue = variant.getSalePrice().multiply(BigDecimal.valueOf(variant.getStockQty()));
        }
        
        return StockReportDTO.builder()
            .variantId(variant.getId())
            .productId(variant.getProduct().getId())
            .productName(variant.getProduct().getName())
            .categoryName(variant.getProduct().getCategory().getName())
            .sku(variant.getSku())
            .colorName(variant.getColor().getName())
            .colorHex(variant.getColor().getHex())
            .typeName(variant.getTypeName())
            .stockQty(variant.getStockQty())
            .price(variant.getPrice())
            .salePrice(variant.getSalePrice())
            .stockValue(stockValue)
            .isActive(variant.getIsActive())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    // ===== UC-MGR-INV-ManageLowStock: QUẢN LÝ SẢN PHẨM TỒN KHO THẤP =====
    
    /**
     * Lấy danh sách sản phẩm tồn kho thấp với thông tin dự báo
     */
    @Transactional(readOnly = true)
    public Page<LowStockProductDTO> getLowStockProducts(Pageable pageable, String keyword) {
        // Lấy sản phẩm có tồn kho <= 5
        List<ProductVariant> variants = productVariantRepository.findAll().stream()
            .filter(v -> v.getStockQty() <= 5 && v.getIsActive())
            .collect(Collectors.toList());
        
        // Lọc theo keyword (tên sản phẩm, SKU, màu sắc, loại sản phẩm)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream()
                .filter(v -> (v.getProduct().getName() != null && v.getProduct().getName().toLowerCase().contains(keywordLower)) ||
                            (v.getSku() != null && v.getSku().toLowerCase().contains(keywordLower)) ||
                            (v.getColor() != null && v.getColor().getName() != null && v.getColor().getName().toLowerCase().contains(keywordLower)) ||
                            (v.getTypeName() != null && v.getTypeName().toLowerCase().contains(keywordLower)))
                .collect(Collectors.toList());
        }
        
        // Convert sang DTO với thông tin dự báo
        List<LowStockProductDTO> lowStockProducts = variants.stream()
            .map(this::mapToLowStockProductDTO)
            .sorted(Comparator.comparingInt(LowStockProductDTO::getActionPriority))
            .collect(Collectors.toList());
        
        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), lowStockProducts.size());
        
        // Kiểm tra start có hợp lệ không
        if (start >= lowStockProducts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), lowStockProducts.size());
        }
        
        List<LowStockProductDTO> pageContent = lowStockProducts.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, lowStockProducts.size());
    }
    
    /**
     * Map ProductVariant sang LowStockProductDTO với thống kê bán hàng
     */
    private LowStockProductDTO mapToLowStockProductDTO(ProductVariant variant) {
        // Tính số lượng đã bán trong 30 ngày qua
        Integer soldLastMonth = calculateSoldLastMonth(variant.getId());
        
        // Tính trung bình bán mỗi ngày
        Double avgDailySales = soldLastMonth != null ? soldLastMonth / 30.0 : 0.0;
        
        return LowStockProductDTO.builder()
            .variantId(variant.getId())
            .productId(variant.getProduct().getId())
            .productName(variant.getProduct().getName())
            .productSlug(variant.getProduct().getSlug())
            .sku(variant.getSku())
            .colorName(variant.getColor().getName())
            .colorHex(variant.getColor().getHex())
            .typeName(variant.getTypeName())
            .stockQty(variant.getStockQty())
            .price(variant.getPrice())
            .salePrice(variant.getSalePrice())
            .isActive(variant.getIsActive())
            .updatedAt(LocalDateTime.now())
            .soldLastMonth(soldLastMonth)
            .avgDailySales(avgDailySales)
            .build();
    }
    
    /**
     * Tính số lượng đã bán trong 30 ngày qua
     */
    private Integer calculateSoldLastMonth(Integer variantId) {
        try {
            String sql = "SELECT COALESCE(SUM(oi.qty), 0) " +
                        "FROM OrderItems oi " +
                        "JOIN Orders o ON o.id = oi.order_id " +
                        "WHERE oi.variant_id = ? " +
                        "AND o.created_at >= DATEADD(DAY, -30, GETDATE()) " +
                        "AND o.status NOT IN ('CANCELLED', 'RETURNED')";
            
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, variantId);
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("Error calculating sold quantity: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Ẩn biến thể sản phẩm tạm thời (chỉ ẩn ProductVariant cụ thể)
     */
    @Transactional
    public void hideProduct(Integer variantId, Integer managerId, String reason) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        
        // Chỉ ẩn biến thể cụ thể này
        variant.setIsActive(false);
        productVariantRepository.save(variant);
        
        String logMessage = String.format(
            "Manager ID %d ẩn biến thể SKU %s của sản phẩm '%s'. Lý do: %s",
            managerId, variant.getSku(), variant.getProduct().getName(), 
            reason != null ? reason : "Không có lý do"
        );
        
        System.out.println("[INVENTORY LOG] " + logMessage);
    }
    
    /**
     * Hiện biến thể sản phẩm trở lại (chỉ hiện ProductVariant cụ thể)
     */
    @Transactional
    public void showProduct(Integer variantId, Integer managerId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        
        // Chỉ hiện biến thể cụ thể này
        variant.setIsActive(true);
        productVariantRepository.save(variant);
        
        String logMessage = String.format(
            "Manager ID %d hiện biến thể SKU %s của sản phẩm '%s' trở lại",
            managerId, variant.getSku(), variant.getProduct().getName()
        );
        
        System.out.println("[INVENTORY LOG] " + logMessage);
    }
    
    /**
     * Lấy danh sách sản phẩm đã ẩn (ProductVariant có is_active = 0)
     */
    public Page<LowStockProductDTO> getHiddenProducts(Pageable pageable, String keyword, Integer categoryId, String stockLevel) {
        // Query cơ bản
        StringBuilder sql = new StringBuilder("""
            SELECT 
                pv.id as variantId,
                p.name as productName,
                p.slug as productSlug,
                pv.sku,
                c.name as colorName,
                pv.type_name as typeName,
                pv.sale_price as salePrice,
                pv.stock_qty as stockQty,
                pv.is_active as isActive,
                p.sold_qty as soldLastMonth,
                CASE 
                    WHEN p.sold_qty > 0 THEN CAST(p.sold_qty AS FLOAT) / 30.0
                    ELSE 0.0
                END as avgDailySales,
                CASE 
                    WHEN pv.stock_qty = 0 THEN NULL
                    WHEN p.sold_qty > 0 THEN CAST(pv.stock_qty AS FLOAT) / (p.sold_qty / 30.0)
                    ELSE NULL
                END as estimatedDaysLeft
            FROM ProductVariant pv
            JOIN Product p ON p.id = pv.product_id
            JOIN Color c ON c.id = pv.color_id
            WHERE pv.is_active = 0
        """);
        
        // Thêm điều kiện lọc
        List<Object> params = new ArrayList<>();
        
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        
        if (stockLevel != null && !stockLevel.isEmpty()) {
            switch (stockLevel) {
                case "out_of_stock":
                    sql.append(" AND pv.stock_qty = 0");
                    break;
                case "low_stock":
                    sql.append(" AND pv.stock_qty BETWEEN 1 AND 5");
                    break;
                case "good_stock":
                    sql.append(" AND pv.stock_qty >= 6");
                    break;
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR pv.sku LIKE ? OR c.name LIKE ? OR pv.type_name LIKE ?)");
            String searchTerm = "%" + keyword.trim() + "%";
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
        }
        
        sql.append(" ORDER BY pv.stock_qty ASC, p.name ASC");
        
        // Thực thi query
        List<LowStockProductDTO> hiddenProducts = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            LowStockProductDTO dto = new LowStockProductDTO();
            dto.setVariantId(rs.getInt("variantId"));
            dto.setProductName(rs.getString("productName"));
            dto.setProductSlug(rs.getString("productSlug"));
            dto.setSku(rs.getString("sku"));
            dto.setColorName(rs.getString("colorName"));
            dto.setTypeName(rs.getString("typeName"));
            dto.setSalePrice(rs.getBigDecimal("salePrice"));
            dto.setStockQty(rs.getInt("stockQty"));
            dto.setIsActive(rs.getBoolean("isActive"));
            dto.setSoldLastMonth(rs.getInt("soldLastMonth"));
            dto.setAvgDailySales(rs.getDouble("avgDailySales"));
            
            Double estimatedDays = rs.getObject("estimatedDaysLeft", Double.class);
            dto.setEstimatedDaysLeft(estimatedDays != null ? estimatedDays.intValue() : null);
            
            return dto;
        });
        
        // Phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), hiddenProducts.size());
        
        if (start >= hiddenProducts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), hiddenProducts.size());
        }
        
        List<LowStockProductDTO> pageContent = hiddenProducts.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, hiddenProducts.size());
    }
    
    /**
     * Lấy thống kê sản phẩm đã ẩn
     */
    public Map<String, Object> getHiddenProductsSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Tổng sản phẩm đã ẩn
        String totalSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0";
        Integer totalHidden = jdbcTemplate.queryForObject(totalSql, Integer.class);
        summary.put("totalHiddenProducts", totalHidden != null ? totalHidden : 0);
        
        // Số sản phẩm hết hàng đã ẩn
        String outOfStockSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0 AND stock_qty = 0";
        Integer outOfStockCount = jdbcTemplate.queryForObject(outOfStockSql, Integer.class);
        summary.put("outOfStockCount", outOfStockCount != null ? outOfStockCount : 0);
        
        // Số sản phẩm tồn kho thấp đã ẩn
        String lowStockSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0 AND stock_qty BETWEEN 1 AND 5";
        Integer lowStockCount = jdbcTemplate.queryForObject(lowStockSql, Integer.class);
        summary.put("lowStockCount", lowStockCount != null ? lowStockCount : 0);
        
        return summary;
    }
}

