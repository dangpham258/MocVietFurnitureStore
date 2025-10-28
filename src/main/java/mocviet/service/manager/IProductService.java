package mocviet.service.manager;

import mocviet.dto.manager.*;
import mocviet.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    // PRODUCT MANAGEMENT
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Integer productId, UpdateProductRequest request);
    void toggleProductActive(Integer productId);

    // VARIANT MANAGEMENT
    ProductVariant addVariant(Integer productId, ProductVariantRequest request);
    void deleteVariant(Integer variantId);
    ProductVariant updateVariant(Integer variantId, ProductVariantRequest request);

    // STOCK & PRICE MANAGEMENT
    void updateStock(Integer variantId, StockUpdateRequest request);
    void updatePrice(Integer variantId, PriceUpdateRequest request);

    // QUERIES
    Page<Product> getProducts(Pageable pageable);
    Page<Product> getActiveProducts(Pageable pageable);
    Page<Product> searchProducts(String keyword, Pageable pageable);
    Page<Product> searchProductsWithCategory(String keyword, Integer categoryId, Pageable pageable);
    Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable);
    Page<Product> getProductsByStatus(Boolean isActive, Pageable pageable);
    Optional<Product> getProductById(Integer productId);
    List<ProductVariant> getProductVariants(Integer productId);
    Optional<ProductVariant> getVariantById(Integer variantId);
    List<Category> getCategories();
    List<Category> getCollections();
    List<Color> getColors();
    List<ProductVariant> getLowStockVariants(Integer threshold);
    List<Product> getLowStockProducts(Integer threshold);
    List<Product> getOutOfStockProducts();

    // IMAGES
    List<ProductImage> getProductImages(Integer productId);
    List<ProductImage> getProductImagesByColor(Integer productId, Integer colorId);
}


