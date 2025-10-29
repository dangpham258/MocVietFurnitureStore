package mocviet.service;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ProductCardDTO;
import mocviet.dto.ProductCriteriaDTO;
import mocviet.dto.ProductDetailDTO;
import mocviet.entity.*;
import mocviet.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Import ObjectMapper nếu bạn dùng (ví dụ: trong findProductDetailBySlug)
// import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    // private final ObjectMapper objectMapper; // Bỏ comment nếu dùng

    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardDTO> findProducts(ProductCriteriaDTO criteria) {
        List<Integer> categoryIds = null;
        if (criteria.getCategorySlug() != null && !criteria.getCategorySlug().isEmpty()) {
             categoryIds = getCategoryIdsIncludingDescendants(criteria.getCategorySlug());
             if (categoryIds.isEmpty()) {
                 return Page.empty();
             }
        }

        Specification<Product> spec = ProductSpecification.findByCriteria(criteria, categoryIds);

        Sort sort;
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "newest";

        if (sortBy.startsWith("price_")) {
            sort = Sort.unsorted(); // Sắp xếp giá đã được xử lý trong Specification
        } else {
            switch (sortBy) {
                case "best_selling":
                    sort = Sort.by(Sort.Direction.DESC, "soldQty");
                    break;
                case "featured":
                    sort = Sort.by(Sort.Direction.DESC, "views");
                    break;
                case "newest":
                default:
                    sort = Sort.by(Sort.Direction.DESC, "createdAt");
                    break;
            }
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // Bước 1: Truy vấn Page<Product> cơ bản
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // Bước 2: Lấy danh sách Product IDs từ trang hiện tại
        List<Integer> productIdsOnPage = productPage.getContent().stream().map(Product::getId).toList();

        // Bước 3: Fetch eager Variants và Images riêng biệt nếu có sản phẩm
        Map<Integer, Product> productMapWithDetails = new HashMap<>();
        if (!productIdsOnPage.isEmpty()) {
            // 3.1: Fetch Variants
            List<Product> productsWithVariants = productRepository.findByIdInWithVariants(productIdsOnPage);
            Map<Integer, List<ProductVariant>> variantsMap = productsWithVariants.stream()
                .collect(Collectors.toMap(Product::getId, p -> p.getVariants() != null ? p.getVariants() : Collections.emptyList())); // Handle null variants

            // 3.2: Fetch Images
            List<Product> productsWithImages = productRepository.findByIdInWithImages(productIdsOnPage);
            Map<Integer, List<ProductImage>> imagesMap = productsWithImages.stream()
                .collect(Collectors.toMap(Product::getId, p -> p.getProductImages() != null ? p.getProductImages() : Collections.emptyList())); // Handle null images

            // 3.3: Gộp thông tin
            for (Product p : productPage.getContent()) {
                p.setVariants(variantsMap.getOrDefault(p.getId(), Collections.emptyList()));
                p.setProductImages(imagesMap.getOrDefault(p.getId(), Collections.emptyList()));
                productMapWithDetails.put(p.getId(), p);
            }
        }

        // Bước 4: Map sang DTO
        Map<Integer, Product> finalProductMap = productMapWithDetails;
        return productPage.map(product -> convertToProductCardDTO(finalProductMap.getOrDefault(product.getId(), product)));
    }


    // --- Các phương thức còn lại (getCategoryIdsIncludingDescendants, ...) giữ nguyên ---
     private List<Integer> getCategoryIdsIncludingDescendants(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return Collections.emptyList();
        }
        Optional<Category> parentCategoryOpt = categoryRepository.findBySlugAndIsActiveTrue(slug);
        if (parentCategoryOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Category parentCategory = parentCategoryOpt.get();
        List<Integer> categoryIds = new ArrayList<>();
        collectCategoryIds(parentCategory, categoryIds);
        return categoryIds;
    }

    private void collectCategoryIds(Category category, List<Integer> ids) {
        if (category == null || (category.getIsActive() != null && !category.getIsActive())) {
            return;
        }
        ids.add(category.getId());
        List<Category> children = categoryRepository.findByParentIdAndIsActiveTrue(category.getId());
        for (Category child : children) {
            collectCategoryIds(child, ids);
        }
    }

    private ProductCardDTO convertToProductCardDTO(Product product) {
        ProductCardDTO dto = new ProductCardDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setAvgRating(product.getAvgRating() != null ? product.getAvgRating().doubleValue() : 0.0);
        dto.setTotalReviews(product.getTotalReviews() != null ? product.getTotalReviews() : 0);

        Optional<ProductVariant> representativeVariant = Optional.empty();
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
             representativeVariant = product.getVariants().stream()
                .filter(v -> v.getIsActive() != null && v.getIsActive())
                .min(Comparator.comparing(pv -> pv.getSalePrice() != null ? pv.getSalePrice() : BigDecimal.ZERO));
        }

        if (representativeVariant.isPresent()) {
            ProductVariant v = representativeVariant.get();
            dto.setOriginalPrice(v.getPrice());
            dto.setSalePrice(v.getSalePrice());
            dto.setDiscountPercent(v.getDiscountPercent() != null ? v.getDiscountPercent() : 0);
            dto.setPromotionType(v.getPromotionType() != null ? v.getPromotionType().name() : null);

            Integer representativeColorId = (v.getColor() != null) ? v.getColor().getId() : null;
            dto.setThumbnailUrl(getThumbnailForProductInternal(product.getProductImages(), product.getSlug(), representativeColorId));

        } else {
            dto.setOriginalPrice(BigDecimal.ZERO);
            dto.setSalePrice(BigDecimal.ZERO);
            dto.setDiscountPercent(0);
            dto.setThumbnailUrl(getThumbnailForProductInternal(product.getProductImages(), product.getSlug(), null));
        }

        return dto;
    }

     private String getThumbnailForProductInternal(List<ProductImage> images, String productSlug, Integer targetColorId) {
         if (images == null) images = Collections.emptyList();

         Optional<ProductImage> targetImage = Optional.empty();

         if (targetColorId != null) {
             targetImage = images.stream()
                .filter(img -> img.getColor() != null && img.getColor().getId().equals(targetColorId))
                .sorted(Comparator.comparing(img -> img.getUrl() != null && img.getUrl().contains("/00_") ? 0 : 1))
                .findFirst();
         }

         if (targetImage.isEmpty()) {
              targetImage = images.stream()
                 .sorted(Comparator.comparing(img -> img.getUrl() != null && img.getUrl().contains("/00_") ? 0 : 1))
                 .findFirst();
         }
         return targetImage.map(ProductImage::getUrl)
                 .orElseGet(() -> createPlaceholderImageUrl(productSlug));
     }

     private String createPlaceholderImageUrl(String slug) {
         String text = (slug != null && !slug.isEmpty()) ? slug.replace("-", "+") : "Moc+Viet";
         return "https://via.placeholder.com/400x400.png?text=" + text;
     }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO findProductDetailBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + slug));

        ProductDetailDTO dto = ProductDetailDTO.fromEntity(product);

        List<ProductVariant> variants = variantRepository.findByProductIdAndIsActiveTrue(product.getId());
        dto.setVariants(variants.stream().map(this::convertToVariantDTO).collect(Collectors.toList()));

        List<ProductImage> images = imageRepository.findByProductId(product.getId());
        Map<Integer, List<String>> imagesByColor = images.stream()
                .filter(img -> img.getColor() != null)
                .collect(Collectors.groupingBy(
                        img -> img.getColor().getId(),
                        Collectors.mapping(
                            this::getActualImageUrl, // Sử dụng hàm trả URL thật
                            Collectors.toList()
                        )
                ));
        dto.setImagesByColor(imagesByColor);

        dto.setColorOptions(variants.stream()
                .map(ProductVariant::getColor)
                .filter(Objects::nonNull)
                .distinct()
                .map(color -> {
                    ProductDetailDTO.ColorOptionDTO opt = new ProductDetailDTO.ColorOptionDTO();
                    opt.setColorId(color.getId());
                    opt.setName(color.getName());
                    opt.setHex(color.getHex());
                    return opt;
                }).collect(Collectors.toList()));

        dto.setTypeOptions(variants.stream()
                .map(ProductVariant::getTypeName)
                .distinct()
                .map(typeName -> {
                    ProductDetailDTO.TypeOptionDTO opt = new ProductDetailDTO.TypeOptionDTO();
                    opt.setTypeName(typeName);
                    return opt;
                }).collect(Collectors.toList()));

        return dto;
    }

    private String getActualImageUrl(ProductImage image) {
        return (image != null && image.getUrl() != null) ? image.getUrl() : "";
    }

     private ProductDetailDTO.VariantDTO convertToVariantDTO(ProductVariant v) {
        ProductDetailDTO.VariantDTO dto = new ProductDetailDTO.VariantDTO();
        dto.setVariantId(v.getId());
        dto.setSku(v.getSku());
        dto.setColorId( (v.getColor() != null) ? v.getColor().getId() : null );
        dto.setTypeName(v.getTypeName());
        dto.setPrice(v.getPrice());
        dto.setSalePrice(v.getSalePrice());
        dto.setDiscountPercent(v.getDiscountPercent() != null ? v.getDiscountPercent() : 0);
        dto.setStockQty(v.getStockQty() != null ? v.getStockQty() : 0);
        dto.setPromotionType(v.getPromotionType() != null ? v.getPromotionType().name() : null);
        return dto;
    }
}