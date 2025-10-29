package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.repository.*;
import mocviet.service.manager.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("managerProductService")
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final ProductImageRepository productImageRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    // ===== PRODUCT MANAGEMENT =====

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
        if (!category.getIsActive()) throw new IllegalArgumentException("Danh mục không đang hoạt động");

        Category collection = null;
        if (request.getCollectionId() != null) {
            collection = categoryRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ sưu tập không tồn tại"));
            if (!collection.getIsActive()) throw new IllegalArgumentException("Bộ sưu tập không đang hoạt động");
        }

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        if (!color.getIsActive()) throw new IllegalArgumentException("Màu sắc không đang hoạt động");

        if (productRepository.existsByName(request.getName()))
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");
        if (variantRepository.existsBySku(request.getSku()))
            throw new IllegalArgumentException("Mã SKU đã tồn tại");

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(generateSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setCollection(collection);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        ProductVariant variant = new ProductVariant();
        variant.setProduct(savedProduct);
        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType() != null ?
                ProductVariant.PromotionType.valueOf(request.getPromotionType()) : null);
        variant.setIsActive(true);
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));

        variantRepository.save(variant);
        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(Integer productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

        Category collection = null;
        if (request.getCollectionId() != null) {
            collection = categoryRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Bộ sưu tập không tồn tại"));
        }

        if (productRepository.existsByNameAndIdNot(request.getName(), productId))
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setCollection(collection);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void toggleProductActive(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }

    // ===== VARIANT MANAGEMENT =====

    @Override
    @Transactional
    public ProductVariant addVariant(Integer productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        if (variantRepository.findByProductAndColorAndType(productId, request.getColorId(), request.getTypeName()).isPresent())
            throw new IllegalArgumentException("Biến thể đã tồn tại");
        if (variantRepository.existsBySku(request.getSku()))
            throw new IllegalArgumentException("Mã SKU đã tồn tại");

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType() != null ?
                ProductVariant.PromotionType.valueOf(request.getPromotionType()) : null);
        variant.setIsActive(true);
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        return variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void deleteVariant(Integer variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        boolean hasCartItems = cartItemRepository.existsByVariantId(variantId);
        boolean hasOrderItems = orderItemRepository.existsByVariantId(variantId);
        if (hasCartItems) throw new IllegalArgumentException("Không thể xóa biến thể đã có trong giỏ hàng");
        if (hasOrderItems) throw new IllegalArgumentException("Không thể xóa biến thể đã có trong đơn hàng");
        variantRepository.delete(variant);
    }

    @Override
    @Transactional
    public ProductVariant updateVariant(Integer variantId, ProductVariantRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));

        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại"));
        if (!color.getIsActive()) {
            throw new IllegalArgumentException("Màu sắc không đang hoạt động");
        }

        if (!request.getSku().equals(variant.getSku()) &&
                variantRepository.existsBySkuAndIdNot(request.getSku(), variantId)) {
            throw new IllegalArgumentException("SKU đã tồn tại");
        }

        if (!request.getTypeName().equals(variant.getTypeName()) ||
                !request.getColorId().equals(variant.getColor().getId())) {
            if (variantRepository.findByProductAndColorAndType(
                    variant.getProduct().getId(), request.getColorId(), request.getTypeName()).isPresent()) {
                throw new IllegalArgumentException("Biến thể với màu sắc và loại này đã tồn tại");
            }
        }

        variant.setColor(color);
        variant.setTypeName(request.getTypeName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setStockQty(request.getStockQty());
        variant.setPromotionType(request.getPromotionType() != null ?
                ProductVariant.PromotionType.valueOf(request.getPromotionType()) : null);
        return variantRepository.save(variant);
    }

    // ===== STOCK MANAGEMENT =====

    @Override
    @Transactional
    public void updateStock(Integer variantId, StockUpdateRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        variant.setStockQty(request.getStockQty());
        variantRepository.save(variant);
    }

    // ===== PRICE MANAGEMENT =====

    @Override
    @Transactional
    public void updatePrice(Integer variantId, PriceUpdateRequest request) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại"));
        variant.setPrice(request.getPrice());
        variant.setDiscountPercent(request.getDiscountPercent());
        variant.setPromotionType(request.getPromotionType() != null ?
                ProductVariant.PromotionType.valueOf(request.getPromotionType()) : null);
        variant.setSalePrice(calculateSalePrice(request.getPrice(), request.getDiscountPercent()));
        variantRepository.save(variant);
    }

    // ===== QUERY METHODS =====

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAllWithCategoryAndCollection(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findActiveProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) return getProducts(pageable);
        String kw = keyword.trim();
        return productRepository.searchProducts(kw, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsWithCategory(String keyword, Integer categoryId, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            if (categoryId != null) return getProductsByCategory(categoryId, pageable);
            return getProducts(pageable);
        }
        String kw = keyword.trim();
        return (categoryId != null)
                ? productRepository.searchProductsInCategory(kw, categoryId, pageable)
                : productRepository.searchProducts(kw, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActive(categoryId, pageable);
    }

    @Override
    public Page<Product> getProductsByStatus(Boolean isActive, Pageable pageable) {
        return productRepository.findByIsActive(isActive, pageable);
    }

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findByIdWithCategoryAndCollection(productId);
    }

    @Override
    public List<ProductVariant> getProductVariants(Integer productId) {
        return variantRepository.findByProductIdWithColor(productId);
    }

    @Override
    public Optional<ProductVariant> getVariantById(Integer variantId) {
        return variantRepository.findById(variantId);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findLeafCategories();
    }

    @Override
    public List<Category> getCollections() {
        return categoryRepository.findByTypeAndActive(Category.CategoryType.COLLECTION);
    }

    @Override
    public List<Color> getColors() {
        return colorRepository.findByIsActiveTrue();
    }

    @Override
    public List<ProductVariant> getLowStockVariants(Integer threshold) {
        return variantRepository.findLowStockVariants(threshold);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    @Override
    public List<ProductImage> getProductImages(Integer productId) {
        return productImageRepository.findByProductIdWithColor(productId);
    }

    @Override
    public List<ProductImage> getProductImagesByColor(Integer productId, Integer colorId) {
        return productImageRepository.findByProductAndColor(productId, colorId);
    }

    // ===== UTILS =====
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
    }

    private BigDecimal calculateSalePrice(BigDecimal price, Integer discountPercent) {
        if (discountPercent == null || discountPercent == 0) return price;
        BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100));
        return price.subtract(discountAmount);
    }
}


