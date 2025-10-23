package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    
    // ===== PRODUCT MANAGEMENT =====
    
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        
        if (!category.getIsActive()) {
            throw new IllegalArgumentException("Danh mục không đang hoạt động");
        }
        
        // Validate collection if provided
        Category collection = null;
        if (request.getCollectionId() != null) {
            collection = categoryRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ sưu tập không tồn tại"));
            if (!collection.getIsActive()) {
                throw new IllegalArgumentException("Bộ sưu tập không đang hoạt động");
            }
        }
        
        // Validate color exists
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        
        if (!color.getIsActive()) {
            throw new IllegalArgumentException("Màu sắc không đang hoạt động");
        }
        
        // Check if product name already exists
        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");
        }
        
        // Check if SKU already exists
        if (variantRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        }
        
        // Create product
        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(generateSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setCollection(collection);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        
        // Create first variant
        ProductVariant variant = new ProductVariant();
        variant.setProduct(savedProduct);
        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType());
        variant.setIsActive(true);
        
        // Calculate sale price
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        
        variantRepository.save(variant);
        
        return savedProduct;
    }
    
    @Transactional
    public Product updateProduct(Integer productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        
        // Validate collection if provided
        Category collection = null;
        if (request.getCollectionId() != null) {
            collection = categoryRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ sưu tập không tồn tại"));
        }
        
        // Check if product name already exists (excluding current product)
        if (productRepository.existsByNameAndIdNot(request.getName(), productId)) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");
        }
        
        // Update product
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setCollection(collection);
        
        return productRepository.save(product);
    }
    
    @Transactional
    public void toggleProductActive(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }
    
    // ===== VARIANT MANAGEMENT =====
    
    @Transactional
    public ProductVariant addVariant(Integer productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        
        // Validate color exists
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        
        // Check if variant already exists
        if (variantRepository.findByProductAndColorAndType(productId, request.getColorId(), request.getTypeName()).isPresent()) {
            throw new IllegalArgumentException("Biến thể đã tồn tại");
        }
        
        // Check if SKU already exists
        if (variantRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        }
        
        // Create variant
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType());
        variant.setIsActive(true);
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        
        return variantRepository.save(variant);
    }
    
    @Transactional
    public ProductVariant updateVariant(Integer variantId, ProductVariantRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        
        // Validate color exists
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        
        // Check if variant already exists (excluding current variant)
        Optional<ProductVariant> existingVariant = variantRepository.findByProductAndColorAndType(
                variant.getProduct().getId(), request.getColorId(), request.getTypeName());
        if (existingVariant.isPresent() && !existingVariant.get().getId().equals(variantId)) {
            throw new IllegalArgumentException("Biến thể đã tồn tại");
        }
        
        // Check if SKU already exists (excluding current variant)
        if (variantRepository.existsBySkuAndIdNot(request.getSku(), variantId)) {
            throw new IllegalArgumentException("Mã SKU đã tồn tại");
        }
        
        // Update variant
        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType());
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        
        return variantRepository.save(variant);
    }
    
    @Transactional
    public void deleteVariant(Integer variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        
        // Check if this is the last variant
        List<ProductVariant> variants = variantRepository.findByProductId(variant.getProduct().getId());
        if (variants.size() <= 1) {
            throw new IllegalArgumentException("Không thể xóa biến thể cuối cùng");
        }
        
        variantRepository.delete(variant);
    }
    
    // ===== STOCK MANAGEMENT =====
    
    @Transactional
    public void updateStock(Integer variantId, StockUpdateRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        
        variant.setStockQty(request.getStockQty());
        variantRepository.save(variant);
    }
    
    // ===== PRICE MANAGEMENT =====
    
    @Transactional
    public void updatePrice(Integer variantId, PriceUpdateRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setPromotionType(request.getPromotionType());
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        
        variantRepository.save(variant);
    }
    
    // ===== QUERY METHODS =====
    
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAllWithCategoryAndCollection(pageable);
    }
    
    
    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findActiveProducts(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActive(categoryId, pageable);
    }
    
    public Page<Product> getProductsByStatus(Boolean isActive, Pageable pageable) {
        return productRepository.findByIsActive(isActive, pageable);
    }
    
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }
    
    public List<ProductVariant> getProductVariants(Integer productId) {
        return variantRepository.findByProductId(productId);
    }
    
    public List<Category> getCategories() {
        return categoryRepository.findByTypeAndActive("CATEGORY");
    }
    
    public List<Category> getCollections() {
        return categoryRepository.findByTypeAndActive("COLLECTION");
    }
    
    public List<Color> getColors() {
        return colorRepository.findActiveColors();
    }
    
    public List<ProductVariant> getLowStockVariants(Integer threshold) {
        return variantRepository.findLowStockVariants(threshold);
    }
    
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
    
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }
    
    // ===== UTILITY METHODS =====
    
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
    }
    
    private BigDecimal calculateSalePrice(BigDecimal price, Integer discountPercent) {
        if (discountPercent == null || discountPercent == 0) {
            return price;
        }
        
        BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100));
        
        return price.subtract(discountAmount);
    }
}
