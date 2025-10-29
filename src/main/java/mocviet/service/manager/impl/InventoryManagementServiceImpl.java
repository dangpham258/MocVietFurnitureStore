package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.ProductVariant;
import mocviet.repository.ProductVariantRepository;
import mocviet.service.manager.IInventoryManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryManagementServiceImpl implements IInventoryManagementService {

    private final ProductVariantRepository productVariantRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public StockSummaryDTO getStockSummary() {
        List<ProductVariant> allVariants = productVariantRepository.findAll();
        long totalProducts = allVariants.size();
        long outOfStock = allVariants.stream().filter(v -> v.getStockQty() == 0 && v.getIsActive()).count();
        long lowStock = allVariants.stream().filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5 && v.getIsActive()).count();
        long inStock = allVariants.stream().filter(v -> v.getStockQty() > 0 && v.getIsActive()).count();
        BigDecimal totalStockValue = allVariants.stream().filter(v -> v.getIsActive() && v.getSalePrice() != null).map(v -> v.getSalePrice().multiply(BigDecimal.valueOf(v.getStockQty()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        return StockSummaryDTO.builder().totalProducts(totalProducts).outOfStockProducts(outOfStock).lowStockProducts(lowStock).inStockProducts(inStock).totalStockValue(totalStockValue).build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockAlertDTO> getStockAlerts(Pageable pageable, String alertType, String keyword) {
        List<ProductVariant> variants;
        if ("OUT_OF_STOCK".equals(alertType)) {
            variants = productVariantRepository.findAll().stream().filter(v -> v.getStockQty() == 0 && v.getIsActive()).collect(Collectors.toList());
        } else if ("LOW_STOCK".equals(alertType)) {
            variants = productVariantRepository.findAll().stream().filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5 && v.getIsActive()).collect(Collectors.toList());
        } else {
            variants = productVariantRepository.findAll().stream().filter(v -> v.getStockQty() <= 5 && v.getIsActive()).collect(Collectors.toList());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream().filter(v -> (v.getProduct().getName() != null && v.getProduct().getName().toLowerCase().contains(keywordLower)) || (v.getSku() != null && v.getSku().toLowerCase().contains(keywordLower)) || (v.getColor() != null && v.getColor().getName() != null && v.getColor().getName().toLowerCase().contains(keywordLower)) || (v.getTypeName() != null && v.getTypeName().toLowerCase().contains(keywordLower))).collect(Collectors.toList());
        }
        List<StockAlertDTO> alerts = variants.stream().map(this::mapToStockAlertDTO).sorted(Comparator.comparingInt(StockAlertDTO::getPriority)).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), alerts.size());
        if (start >= alerts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), alerts.size());
        }
        List<StockAlertDTO> pageContent = alerts.subList(start, end);
        return new PageImpl<>(pageContent, pageable, alerts.size());
    }

    @Override
    @Transactional
    public void updateStock(UpdateStockRequest request, Integer managerId) {
        ProductVariant variant = productVariantRepository.findById(request.getVariantId()).orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + request.getVariantId()));
        if (!variant.getIsActive()) throw new RuntimeException("Không thể cập nhật tồn kho cho sản phẩm đã vô hiệu hóa");
        if (request.getNewStockQty() < 0) throw new RuntimeException("Số lượng tồn kho phải >= 0");
        Integer oldStockQty = variant.getStockQty();
        variant.setStockQty(request.getNewStockQty());
        productVariantRepository.save(variant);
        String logMessage = String.format("Manager ID %d cập nhật tồn kho SKU %s từ %d -> %d. Ghi chú: %s", managerId, variant.getSku(), oldStockQty, request.getNewStockQty(), request.getNote() != null ? request.getNote() : "Không có ghi chú");
        System.out.println("[INVENTORY LOG] " + logMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public StockAlertDTO getVariantDetails(Integer variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        return mapToStockAlertDTO(variant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockReportDTO> getStockReport(Pageable pageable, Integer categoryId, String stockLevel, String keyword) {
        List<ProductVariant> variants = productVariantRepository.findAll();
        if (categoryId != null) {
            variants = variants.stream().filter(v -> v.getProduct().getCategory().getId().equals(categoryId)).collect(Collectors.toList());
        }
        if (stockLevel != null && !stockLevel.isEmpty()) {
            switch (stockLevel) {
                case "OUT_OF_STOCK":
                    variants = variants.stream().filter(v -> v.getStockQty() == 0).collect(Collectors.toList());
                    break;
                case "LOW_STOCK":
                    variants = variants.stream().filter(v -> v.getStockQty() > 0 && v.getStockQty() <= 5).collect(Collectors.toList());
                    break;
                case "MEDIUM_STOCK":
                    variants = variants.stream().filter(v -> v.getStockQty() > 5 && v.getStockQty() <= 20).collect(Collectors.toList());
                    break;
                case "GOOD_STOCK":
                    variants = variants.stream().filter(v -> v.getStockQty() > 20).collect(Collectors.toList());
                    break;
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream().filter(v -> v.getProduct().getName().toLowerCase().contains(keywordLower) || v.getSku().toLowerCase().contains(keywordLower) || v.getColor().getName().toLowerCase().contains(keywordLower) || v.getTypeName().toLowerCase().contains(keywordLower)).collect(Collectors.toList());
        }
        List<StockReportDTO> reports = variants.stream().map(this::mapToStockReportDTO).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), reports.size());
        if (start >= reports.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), reports.size());
        }
        List<StockReportDTO> pageContent = reports.subList(start, end);
        return new PageImpl<>(pageContent, pageable, reports.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LowStockProductDTO> getLowStockProducts(Pageable pageable, String keyword) {
        List<ProductVariant> variants = productVariantRepository.findAll().stream().filter(v -> v.getStockQty() <= 5 && v.getIsActive()).collect(Collectors.toList());
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.trim().toLowerCase();
            variants = variants.stream().filter(v -> (v.getProduct().getName() != null && v.getProduct().getName().toLowerCase().contains(keywordLower)) || (v.getSku() != null && v.getSku().toLowerCase().contains(keywordLower)) || (v.getColor() != null && v.getColor().getName() != null && v.getColor().getName().toLowerCase().contains(keywordLower)) || (v.getTypeName() != null && v.getTypeName().toLowerCase().contains(keywordLower))).collect(Collectors.toList());
        }
        List<LowStockProductDTO> lowStockProducts = variants.stream().map(this::mapToLowStockProductDTO).sorted(Comparator.comparingInt(LowStockProductDTO::getActionPriority)).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), lowStockProducts.size());
        if (start >= lowStockProducts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), lowStockProducts.size());
        }
        List<LowStockProductDTO> pageContent = lowStockProducts.subList(start, end);
        return new PageImpl<>(pageContent, pageable, lowStockProducts.size());
    }

    @Override
    @Transactional
    public void hideProduct(Integer variantId, Integer managerId, String reason) {
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        variant.setIsActive(false);
        productVariantRepository.save(variant);
        String logMessage = String.format("Manager ID %d ẩn biến thể SKU %s của sản phẩm '%s'. Lý do: %s", managerId, variant.getSku(), variant.getProduct().getName(), reason != null ? reason : "Không có lý do");
        System.out.println("[INVENTORY LOG] " + logMessage);
    }

    @Override
    @Transactional
    public void showProduct(Integer variantId, Integer managerId) {
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với ID: " + variantId));
        variant.setIsActive(true);
        productVariantRepository.save(variant);
        String logMessage = String.format("Manager ID %d hiện biến thể SKU %s của sản phẩm '%s' trở lại", managerId, variant.getSku(), variant.getProduct().getName());
        System.out.println("[INVENTORY LOG] " + logMessage);
    }

    @Override
    public Page<LowStockProductDTO> getHiddenProducts(Pageable pageable, String keyword, Integer categoryId, String stockLevel) {
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
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), hiddenProducts.size());
        if (start >= hiddenProducts.size()) {
            start = 0;
            end = Math.min(pageable.getPageSize(), hiddenProducts.size());
        }
        List<LowStockProductDTO> pageContent = hiddenProducts.subList(start, end);
        return new PageImpl<>(pageContent, pageable, hiddenProducts.size());
    }

    @Override
    public Map<String, Object> getHiddenProductsSummary() {
        Map<String, Object> summary = new HashMap<>();
        String totalSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0";
        Integer totalHidden = jdbcTemplate.queryForObject(totalSql, Integer.class);
        summary.put("totalHiddenProducts", totalHidden != null ? totalHidden : 0);
        String outOfStockSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0 AND stock_qty = 0";
        Integer outOfStockCount = jdbcTemplate.queryForObject(outOfStockSql, Integer.class);
        summary.put("outOfStockCount", outOfStockCount != null ? outOfStockCount : 0);
        String lowStockSql = "SELECT COUNT(*) FROM ProductVariant WHERE is_active = 0 AND stock_qty BETWEEN 1 AND 5";
        Integer lowStockCount = jdbcTemplate.queryForObject(lowStockSql, Integer.class);
        summary.put("lowStockCount", lowStockCount != null ? lowStockCount : 0);
        return summary;
    }

    private StockAlertDTO mapToStockAlertDTO(ProductVariant variant) {
        String alertLevel = variant.getStockQty() == 0 ? "OUT_OF_STOCK" : "LOW_STOCK";
        return StockAlertDTO.builder().variantId(variant.getId()).productId(variant.getProduct().getId()).productName(variant.getProduct().getName()).sku(variant.getSku()).colorName(variant.getColor().getName()).colorHex(variant.getColor().getHex()).typeName(variant.getTypeName()).stockQty(variant.getStockQty()).price(variant.getPrice()).salePrice(variant.getSalePrice()).alertLevel(alertLevel).updatedAt(LocalDateTime.now()).isActive(variant.getIsActive()).build();
    }

    private StockReportDTO mapToStockReportDTO(ProductVariant variant) {
        BigDecimal stockValue = BigDecimal.ZERO;
        if (variant.getSalePrice() != null) {
            stockValue = variant.getSalePrice().multiply(BigDecimal.valueOf(variant.getStockQty()));
        }
        return StockReportDTO.builder().variantId(variant.getId()).productId(variant.getProduct().getId()).productName(variant.getProduct().getName()).categoryName(variant.getProduct().getCategory().getName()).sku(variant.getSku()).colorName(variant.getColor().getName()).colorHex(variant.getColor().getHex()).typeName(variant.getTypeName()).stockQty(variant.getStockQty()).price(variant.getPrice()).salePrice(variant.getSalePrice()).stockValue(stockValue).isActive(variant.getIsActive()).updatedAt(LocalDateTime.now()).build();
    }

    private LowStockProductDTO mapToLowStockProductDTO(ProductVariant variant) {
        Integer soldLastMonth = calculateSoldLastMonth(variant.getId());
        Double avgDailySales = soldLastMonth != null ? soldLastMonth / 30.0 : 0.0;
        return LowStockProductDTO.builder().variantId(variant.getId()).productId(variant.getProduct().getId()).productName(variant.getProduct().getName()).productSlug(variant.getProduct().getSlug()).sku(variant.getSku()).colorName(variant.getColor().getName()).colorHex(variant.getColor().getHex()).typeName(variant.getTypeName()).stockQty(variant.getStockQty()).price(variant.getPrice()).salePrice(variant.getSalePrice()).isActive(variant.getIsActive()).updatedAt(LocalDateTime.now()).soldLastMonth(soldLastMonth).avgDailySales(avgDailySales).build();
    }

    private Integer calculateSoldLastMonth(Integer variantId) {
        try {
            String sql = "SELECT COALESCE(SUM(oi.qty), 0) FROM OrderItems oi JOIN Orders o ON o.id = oi.order_id WHERE oi.variant_id = ? AND o.created_at >= DATEADD(DAY, -30, GETDATE()) AND o.status NOT IN ('CANCELLED', 'RETURNED')";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, variantId);
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("Error calculating sold quantity: " + e.getMessage());
            return 0;
        }
    }
}


