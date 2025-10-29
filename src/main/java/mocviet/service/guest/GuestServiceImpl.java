package mocviet.service.guest;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ArticleSummaryDTO;
import mocviet.dto.GuestMessageRequestDTO;
import mocviet.dto.MessageResponse;
import mocviet.dto.ProductCardDTO;
import mocviet.entity.*;
import mocviet.repository.*;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mocviet.repository.ArticleRepository; // Đảm bảo import đúng
import mocviet.entity.StaticPage;              // Thêm import
import mocviet.repository.StaticPageRepository; // Thêm import

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements IGuestService {

    // ... (các repository khác) ...
    private final BannerRepository bannerRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository imageRepository;
    private final ArticleRepository articleRepository; // <<--- Đã có
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final StaticPageRepository staticPageRepository; // <<<--- Thêm StaticPageRepository

    // ... (các phương thức getActiveBanners, getFeaturedProducts, ...)
    @Override
    @Transactional(readOnly = true)
    public Optional<StaticPage> getStaticPageBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return Optional.empty();
        }
        return staticPageRepository.findBySlugAndIsActiveTrue(slug);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryDTO> findArticlesByType(Article.ArticleType type, Pageable pageable) {
        // SỬ DỤNG TÊN ĐÚNG: findByArticleTypeAndStatusTrue
        Page<Article> articlePage = articleRepository.findByArticleTypeAndStatusTrue(type, pageable); // <<<--- SỬA Ở ĐÂY
        return articlePage.map(ArticleSummaryDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryDTO> findAllArticles(Pageable pageable) {
         // SỬ DỤNG TÊN ĐÚNG: findByStatusTrue
        Page<Article> articlePage = articleRepository.findByStatusTrue(pageable); // <<<--- SỬA Ở ĐÂY
        return articlePage.map(ArticleSummaryDTO::fromEntity);
    }

    // ... (các phương thức và helper khác giữ nguyên) ...

     @Override
    @Transactional(readOnly = true)
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "views"));
        // Sử dụng findAll vì Specification đã lọc active=true
        return productRepository.findAll(pageable).stream()
                .map(this::convertToProductCardDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Sử dụng findAll vì Specification đã lọc active=true
        return productRepository.findAll(pageable).stream()
                .map(this::convertToProductCardDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getTopDiscountedProducts(int limit) {
        Pageable variantPageable = PageRequest.of(0, limit * 2);
        List<ProductVariant> topVariants = variantRepository.findTopDiscountedActiveVariants(variantPageable);

        List<Integer> productIds = topVariants.stream()
                .map(v -> v.getProduct().getId())
                .distinct()
                .limit(limit)
                .toList();

        List<Product> products = productRepository.findByIdInWithVariants(productIds);

        products.sort(Comparator.comparing(
                (Product p) -> p.getVariants().stream()
                        .filter(v -> v.getIsActive() && v.getDiscountPercent() != null && v.getDiscountPercent() > 0)
                        .mapToInt(pv -> pv.getDiscountPercent() != null ? pv.getDiscountPercent() : 0) // Handle null
                        .max()
                        .orElse(0),
                Comparator.reverseOrder()
        ));

        // Cần fetch images riêng
        Map<Integer, List<ProductImage>> imagesMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<Product> productsWithImages = productRepository.findByIdInWithImages(productIds);
            imagesMap = productsWithImages.stream()
                .collect(Collectors.toMap(Product::getId, p -> p.getProductImages() != null ? p.getProductImages() : Collections.emptyList()));
        }

        Map<Integer, List<ProductImage>> finalImagesMap = imagesMap; // effectively final
        return products.stream()
                .map(p -> {
                    // Gán images vào product trước khi convert
                    p.setProductImages(finalImagesMap.getOrDefault(p.getId(), Collections.emptyList()));
                    return convertToProductCardDTO(p);
                 })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getBestSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "soldQty"));
         // Sử dụng findAll vì Specification đã lọc active=true
        return productRepository.findAll(pageable).stream()
                .map(this::convertToProductCardDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<Review> getBestReviews(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Review> reviews = reviewRepository.findTopBestReviewsDistinctProduct(pageable);
        for (Review review : reviews) {
            if (review.getUser() != null) Hibernate.initialize(review.getUser());
            if (review.getProduct() != null) Hibernate.initialize(review.getProduct());
        }
        return reviews;
    }

    @Override
    @Transactional
    public MessageResponse handleGuestMessage(GuestMessageRequestDTO messageRequest) {
        try {
            Optional<Conversation> existingConversationOpt = conversationRepository
                    .findByGuestEmailAndStatus(messageRequest.getGuestEmail(), Conversation.ConversationStatus.OPEN);

            Conversation conversation;
            if (existingConversationOpt.isPresent()) {
                conversation = existingConversationOpt.get();
                conversation.setGuestName(messageRequest.getGuestName());
            } else {
                conversation = new Conversation();
                conversation.setGuestName(messageRequest.getGuestName());
                conversation.setGuestEmail(messageRequest.getGuestEmail());
                conversation.setStatus(Conversation.ConversationStatus.OPEN);
                conversation = conversationRepository.save(conversation);
            }

            Message message = new Message();
            message.setConversation(conversation);
            message.setSenderType(Message.SenderType.GUEST);
            message.setSender(null);
            message.setContent(messageRequest.getContent());

            messageRepository.save(message);
            return MessageResponse.success("Tin nhắn của bạn đã được gửi thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.error("Đã xảy ra lỗi khi gửi tin nhắn. Vui lòng thử lại.");
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

    private String getPlaceholderImageUrl(ProductImage image) {
        if (image != null && image.getUrl() != null) {
            return image.getUrl();
        }
        String slug = (image != null && image.getProduct() != null) ? image.getProduct().getSlug() : null;
        return createPlaceholderImageUrl(slug);
    }
}