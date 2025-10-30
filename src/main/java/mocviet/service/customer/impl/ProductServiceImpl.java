package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.ProductCardDTO;
import mocviet.dto.customer.ProductCriteriaDTO;
import mocviet.dto.customer.ProductDetailDTO;
import mocviet.dto.customer.ReviewDTO;
import mocviet.entity.Color; // Thêm import
import mocviet.entity.Product;
import mocviet.entity.ProductImage;
import mocviet.entity.ProductVariant;
import mocviet.repository.ProductImageRepository;
import mocviet.repository.CategoryRepository;
import mocviet.repository.ProductRepository;
import mocviet.repository.ProductVariantRepository;
import mocviet.repository.ReviewRepository;
import mocviet.service.ProductSpecification;
import mocviet.service.customer.IProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper; // Thêm import nếu dùng ObjectMapper

import java.math.BigDecimal;
import java.util.Comparator; // Thêm import
import java.util.List;
import java.util.Map;
import java.util.Objects; // Thêm import
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    // private final ObjectMapper objectMapper; // Bỏ comment nếu dùng

    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardDTO> findProducts(ProductCriteriaDTO criteria) {

        // Xác định categoryIds bao gồm cả danh mục con (nếu có)
        List<Integer> categoryIds = null;
        if (criteria.getCategorySlug() != null && !criteria.getCategorySlug().isBlank()) {
            categoryIds = getCategoryIdsIncludingDescendants(criteria.getCategorySlug());
            if (categoryIds.isEmpty()) {
                return Page.empty();
            }
        }

        // 1. Tạo Specification (đã bao gồm orderBy giá nếu cần)
        Specification<Product> spec = ProductSpecification.findByCriteria(criteria, categoryIds);

        // 2. Tạo Sort (CHỈ khi KHÔNG sort theo giá)
        Sort sort;
        if (criteria.getSortBy().startsWith("price_")) {
            // Đã xử lý trong Specification bằng MIN/MAX, dùng Unsorted
            sort = Sort.unsorted();
        } else {
            // Các trường hợp sort khác
            sort = switch (criteria.getSortBy()) {
                case "best_selling" -> Sort.by(Sort.Direction.DESC, "soldQty");
                case "featured" -> Sort.by(Sort.Direction.DESC, "views");
                default -> Sort.by(Sort.Direction.DESC, "createdAt"); // newest
            };
            // Thêm ID để ổn định
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id"));
        }

        // 3. Tạo Pageable
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // 4. Truy vấn
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // 5. Chuyển đổi sang DTO
        return productPage.map(this::convertToProductCardDTO);
    }

    // --- Cập nhật convertToProductCardDTO ---
    private ProductCardDTO convertToProductCardDTO(Product product) {
        ProductCardDTO dto = new ProductCardDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setAvgRating(product.getAvgRating() != null ? product.getAvgRating().doubleValue() : 0.0);
        dto.setTotalReviews(product.getTotalReviews());

        // Tìm variant đại diện (ví dụ: giá thấp nhất) để hiển thị trên card
        Optional<ProductVariant> representativeVariant = Optional.empty();
        if (product.getVariants() != null) { // Kiểm tra null collection
             representativeVariant = product.getVariants().stream()
                .filter(v -> v.getIsActive() != null && v.getIsActive())
                // Ưu tiên variant có giá sale thấp nhất
                .min(Comparator.comparing(ProductVariant::getSalePrice));
        }


        if (representativeVariant.isPresent()) {
            ProductVariant v = representativeVariant.get();
            dto.setOriginalPrice(v.getPrice());
            dto.setSalePrice(v.getSalePrice());
            dto.setDiscountPercent(v.getDiscountPercent());
            dto.setPromotionType(v.getPromotionType() != null ? v.getPromotionType().name() : null);

             // Lấy ảnh thumbnail dựa vào màu của variant đại diện
             Integer representativeColorId = (v.getColor() != null) ? v.getColor().getId() : null;
             dto.setThumbnailUrl(getThumbnailForProduct(product, representativeColorId));

        } else {
            // Trường hợp không có variant active nào
            dto.setOriginalPrice(BigDecimal.ZERO);
            dto.setSalePrice(BigDecimal.ZERO);
            dto.setThumbnailUrl(getThumbnailForProduct(product, null)); // Lấy ảnh mặc định
        }

        return dto;
    }

     // --- Helper lấy thumbnail ---
     private String getThumbnailForProduct(Product product, Integer targetColorId) {
         // Lấy danh sách ảnh (nên tối ưu bằng cách fetch EAGER hoặc join fetch nếu cần)
         // Tạm thời gọi repo ở đây, CẨN THẬN vấn đề N+1 query
         List<ProductImage> images = imageRepository.findByProductId(product.getId());

         Optional<ProductImage> targetImage = Optional.empty();

         // Ưu tiên ảnh có màu khớp targetColorId
         if (targetColorId != null) {
             targetImage = images.stream()
                .filter(img -> img.getColor() != null && img.getColor().getId().equals(targetColorId))
                 // Ưu tiên ảnh có tên bắt đầu bằng "00_" làm thumbnail
                .sorted(Comparator.comparing(img -> img.getUrl().contains("/00_") ? 0 : 1))
                .findFirst();
         }

         // Nếu không có ảnh màu khớp, lấy ảnh "00_" bất kỳ hoặc ảnh đầu tiên
         if (targetImage.isEmpty()) {
              targetImage = images.stream()
                 .sorted(Comparator.comparing(img -> img.getUrl().contains("/00_") ? 0 : 1))
                 .findFirst();
         }

        // Nếu có ảnh, trả về URL ảnh thật trong static; nếu không có, trả về null để view tự fallback
        if (targetImage.isPresent()) {
            return targetImage.get().getUrl();
        } else {
            return null;
        }
     }

    // --- findProductDetailBySlug và các hàm helper khác ---
    // (Giữ nguyên hoặc chỉnh sửa nhỏ nếu cần, đảm bảo import đầy đủ)

     @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO findProductDetailBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + slug));

        ProductDetailDTO dto = ProductDetailDTO.fromEntity(product);

        List<ProductVariant> variants = variantRepository.findByProductIdAndIsActiveTrue(product.getId());
        dto.setVariants(variants.stream().map(this::convertToVariantDTO).collect(Collectors.toList()));

        List<ProductImage> images = imageRepository.findByProductId(product.getId());
         // Grouping ảnh theo color ID
        Map<Integer, List<String>> imagesByColor = images.stream()
                .filter(img -> img.getColor() != null) // Bỏ qua ảnh không có màu (nếu có)
                .collect(Collectors.groupingBy(
                        img -> img.getColor().getId(),
                        Collectors.mapping(ProductImage::getUrl, Collectors.toList())
                ));
        dto.setImagesByColor(imagesByColor);

        // Lấy các lựa chọn màu từ variant active
        dto.setColorOptions(variants.stream()
                .map(ProductVariant::getColor)
                .filter(Objects::nonNull) // Bỏ qua variant không có màu
                .distinct() // Lấy màu duy nhất
                .map(color -> {
                    ProductDetailDTO.ColorOptionDTO opt = new ProductDetailDTO.ColorOptionDTO();
                    opt.setColorId(color.getId());
                    opt.setName(color.getName());
                    opt.setHex(color.getHex());
                    return opt;
                }).collect(Collectors.toList()));

        // Lấy các lựa chọn loại từ variant active
        dto.setTypeOptions(variants.stream()
                .map(ProductVariant::getTypeName)
                .distinct() // Lấy loại duy nhất
                .map(typeName -> {
                    ProductDetailDTO.TypeOptionDTO opt = new ProductDetailDTO.TypeOptionDTO();
                    opt.setTypeName(typeName);
                    return opt;
                }).collect(Collectors.toList()));

        // Thêm: map reviews công khai
        List<mocviet.entity.Review> reviews = reviewRepository.findByProductIdAndIsHiddenFalse(product.getId());
        List<ReviewDTO> reviewDTOs = reviews.stream()
            .map(r -> {
                ReviewDTO dtoR = new ReviewDTO();
                dtoR.setId(r.getId());
                dtoR.setOrderItemId(r.getOrderItem() != null ? r.getOrderItem().getId() : null);
                dtoR.setRating(r.getRating());
                dtoR.setContent(r.getContent());
                dtoR.setImageUrl(r.getImageUrl());
                dtoR.setCreatedAt(r.getCreatedAt());
                dtoR.setManagerRespone(r.getManagerResponse());
                // Lấy tên khách đánh giá (ưu tiên User, fallback là receiverName trong Address của Orders)
                String userName = "Khách";
                if (r.getUser() != null && r.getUser().getFullName() != null) {
                    userName = r.getUser().getFullName();
                } else if (r.getOrderItem() != null
                        && r.getOrderItem().getOrder() != null
                        && r.getOrderItem().getOrder().getAddress() != null
                        && r.getOrderItem().getOrder().getAddress().getReceiverName() != null) {
                    userName = r.getOrderItem().getOrder().getAddress().getReceiverName();
                }
                dtoR.setUserFullName(userName);
                return dtoR;
            })
            .collect(Collectors.toList());
        dto.setReviews(reviewDTOs);

        return dto;
    }

    // == Helpers cho category descendants ==
    private List<Integer> getCategoryIdsIncludingDescendants(String slug) {
        if (slug == null || slug.trim().isEmpty()) return java.util.Collections.emptyList();
        java.util.Optional<mocviet.entity.Category> parentOpt = categoryRepository.findBySlugAndIsActiveTrue(slug);
        if (parentOpt.isEmpty()) return java.util.Collections.emptyList();
        mocviet.entity.Category parent = parentOpt.get();
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        collectCategoryIds(parent, ids);
        return ids;
    }

    private void collectCategoryIds(mocviet.entity.Category category, java.util.List<Integer> ids) {
        if (category == null || (category.getIsActive() != null && !category.getIsActive())) return;
        ids.add(category.getId());
        java.util.List<mocviet.entity.Category> children = categoryRepository.findByParentIdAndIsActiveTrue(category.getId());
        for (mocviet.entity.Category child : children) {
            collectCategoryIds(child, ids);
        }
    }

     private ProductDetailDTO.VariantDTO convertToVariantDTO(ProductVariant v) {
        ProductDetailDTO.VariantDTO dto = new ProductDetailDTO.VariantDTO();
        dto.setVariantId(v.getId());
        dto.setSku(v.getSku());
        // Lấy colorId an toàn
        dto.setColorId( (v.getColor() != null) ? v.getColor().getId() : null );
        dto.setTypeName(v.getTypeName());
        dto.setPrice(v.getPrice());
        dto.setSalePrice(v.getSalePrice());
        dto.setDiscountPercent(v.getDiscountPercent());
        dto.setStockQty(v.getStockQty());
        dto.setPromotionType(v.getPromotionType() != null ? v.getPromotionType().name() : null);
        return dto;
    }

     // Hàm tạo ảnh tạm (giữ nguyên)
    private String getPlaceholderImageUrl(ProductImage image) {
        if (image == null || image.getProduct() == null || image.getColor() == null) {
            return "https://via.placeholder.com/800x800.png?text=Invalid+Image+Data";
        }
        String text = image.getProduct().getSlug().replace("-", "+");
        String color = image.getColor().getSlug();
        // Có thể thay đổi kích thước nếu cần
        return "https://via.placeholder.com/800x800.png?text=" + text + "+(Mau:+" + color + ")";
    }
}