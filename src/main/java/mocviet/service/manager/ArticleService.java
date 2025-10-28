package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Article;
import mocviet.entity.Article.ArticleType;
import mocviet.entity.ArticleImage;
import mocviet.entity.Product;
import mocviet.repository.ArticleImageRepository;
import mocviet.repository.ArticleRepository;
import mocviet.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final ProductRepository productRepository;
    
    /**
     * Tạo bài viết mới
     */
    @Transactional
    public Article createArticle(CreateArticleRequest request, String authorUsername) {
        // Validate article type
        ArticleType articleType;
        try {
            articleType = ArticleType.valueOf(request.getArticleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ. Chỉ chấp nhận: MEDIA, NEWS, PEOPLE");
        }
        
        // Validate linked product if provided
        Product linkedProduct = null;
        if (request.getLinkedProductId() != null) {
            linkedProduct = productRepository.findById(request.getLinkedProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm liên quan không tồn tại"));
        }
        
        // Generate slug from title
        String slug = generateSlug(request.getTitle());
        
        // Ensure slug is unique
        slug = ensureUniqueSlug(slug);
        
        // Create article
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(slug);
        article.setArticleType(articleType);
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setAuthor(authorUsername);
        article.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        article.setStatus(request.getStatus() != null ? request.getStatus() : false);
        article.setLinkedProduct(linkedProduct);
        article.setViews(0);
        article.setCreatedAt(LocalDateTime.now());
        
        // Set published_at if status is true (xuất bản)
        if (article.getStatus()) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        return articleRepository.save(article);
    }
    
    /**
     * Cập nhật bài viết
     */
    @Transactional
    public Article updateArticle(UpdateArticleRequest request, String authorUsername) {
        Article article = articleRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        
        // Kiểm tra quyền sở hữu: chỉ tác giả mới được sửa
        if (!article.getAuthor().equals(authorUsername)) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài viết này");
        }
        
        // Validate article type
        ArticleType articleType;
        try {
            articleType = ArticleType.valueOf(request.getArticleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ. Chỉ chấp nhận: MEDIA, NEWS, PEOPLE");
        }
        
        // Validate linked product if provided
        Product linkedProduct = null;
        if (request.getLinkedProductId() != null) {
            linkedProduct = productRepository.findById(request.getLinkedProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm liên quan không tồn tại"));
        }
        
        // Nếu title thay đổi, cần tạo slug mới
        String newSlug = article.getSlug();
        if (!article.getTitle().equals(request.getTitle())) {
            newSlug = generateSlug(request.getTitle());
            // Ensure unique (ngoại trừ chính nó)
            newSlug = ensureUniqueSlugExcept(newSlug, article.getId());
        }
        
        // Lưu old status để kiểm tra có thay đổi không
        Boolean oldStatus = article.getStatus();
        
        // Update fields
        article.setTitle(request.getTitle());
        article.setSlug(newSlug);
        article.setArticleType(articleType);
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        article.setStatus(request.getStatus() != null ? request.getStatus() : false);
        article.setLinkedProduct(linkedProduct);
        
        // Nếu chuyển từ nháp (false) sang xuất bản (true), set published_at
        if (!oldStatus && article.getStatus() && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        
        return articleRepository.save(article);
    }
    
    /**
     * Lấy danh sách bài viết của manager với phân trang và bộ lọc
     */
    @Transactional(readOnly = true)
    public Page<ArticleListDTO> getArticlesByAuthor(String authorUsername, 
                                                     String articleType,
                                                     Boolean status,
                                                     String keyword,
                                                     Pageable pageable) {
        ArticleType type = null;
        if (articleType != null && !articleType.isEmpty()) {
            try {
                type = ArticleType.valueOf(articleType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid type
            }
        }
        
        Page<Article> articles = articleRepository.findByAuthorWithFilters(
                authorUsername, type, status, keyword, pageable);
        
        return articles.map(this::convertToListDTO);
    }
    
    /**
     * Lấy chi tiết bài viết (chỉ của chính mình)
     */
    @Transactional(readOnly = true)
    public ArticleDetailDTO getArticleDetail(Integer id, String authorUsername) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        
        // Kiểm tra quyền sở hữu
        if (!article.getAuthor().equals(authorUsername)) {
            throw new IllegalArgumentException("Bạn không có quyền xem bài viết này");
        }
        
        return convertToDetailDTO(article);
    }
    
    /**
     * Lấy thống kê dashboard cho manager
     */
    @Transactional(readOnly = true)
    public ArticleDashboardDTO getDashboard(String authorUsername) {
        long totalArticles = articleRepository.countByAuthor(authorUsername);
        long publishedArticles = articleRepository.countByAuthorAndStatus(authorUsername, true);
        long draftArticles = articleRepository.countDraftsByAuthor(authorUsername);
        Long totalViews = articleRepository.sumViewsByAuthor(authorUsername);
        
        return new ArticleDashboardDTO(
                totalArticles,
                publishedArticles,
                draftArticles,
                totalViews != null ? totalViews : 0L
        );
    }
    
    /**
     * Lấy danh sách sản phẩm active (để chọn linked product)
     */
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::getIsActive)
                .collect(Collectors.toList());
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Generate slug from title (Vietnamese-friendly)
     */
    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled";
        }
        
        // Normalize Unicode (NFD = decompose)
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        
        // Remove diacritics
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacritics = pattern.matcher(normalized).replaceAll("");
        
        // Convert to lowercase, replace đ with d
        String slug = withoutDiacritics.toLowerCase(Locale.ROOT)
                .replace('đ', 'd')
                .replace('Đ', 'd');
        
        // Replace non-alphanumeric with hyphens
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        
        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");
        
        // Limit length to 290 chars (database allows 300, leave room for suffixes)
        if (slug.length() > 290) {
            slug = slug.substring(0, 290);
            // Remove trailing hyphen if any
            slug = slug.replaceAll("-+$", "");
        }
        
        return slug.isEmpty() ? "untitled" : slug;
    }
    
    /**
     * Ensure slug is unique by appending number if needed
     */
    private String ensureUniqueSlug(String slug) {
        String originalSlug = slug;
        int counter = 1;
        
        while (articleRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    /**
     * Ensure slug is unique except for the given article id
     */
    private String ensureUniqueSlugExcept(String slug, Integer articleId) {
        Optional<Article> existing = articleRepository.findBySlug(slug);
        
        if (existing.isEmpty() || existing.get().getId().equals(articleId)) {
            return slug;
        }
        
        String originalSlug = slug;
        int counter = 1;
        
        while (true) {
            slug = originalSlug + "-" + counter;
            existing = articleRepository.findBySlug(slug);
            
            if (existing.isEmpty() || existing.get().getId().equals(articleId)) {
                return slug;
            }
            counter++;
        }
    }
    
    /**
     * Convert Article entity to ArticleListDTO
     */
    private ArticleListDTO convertToListDTO(Article article) {
        ArticleListDTO dto = new ArticleListDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setArticleType(article.getArticleType().name());
        dto.setSummary(article.getSummary());
        dto.setThumbnail(article.getThumbnail());
        dto.setAuthor(article.getAuthor());
        dto.setViews(article.getViews());
        dto.setIsFeatured(article.getIsFeatured());
        dto.setStatus(article.getStatus());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCreatedAt(article.getCreatedAt());
        
        if (article.getLinkedProduct() != null) {
            dto.setLinkedProductId(article.getLinkedProduct().getId());
            dto.setLinkedProductName(article.getLinkedProduct().getName());
        }
        
        return dto;
    }
    
    /**
     * Convert Article entity to ArticleDetailDTO
     */
    private ArticleDetailDTO convertToDetailDTO(Article article) {
        ArticleDetailDTO dto = new ArticleDetailDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setArticleType(article.getArticleType().name());
        dto.setSummary(article.getSummary());
        dto.setContent(article.getContent());
        dto.setThumbnail(article.getThumbnail());
        dto.setAuthor(article.getAuthor());
        dto.setViews(article.getViews());
        dto.setIsFeatured(article.getIsFeatured());
        dto.setStatus(article.getStatus());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCreatedAt(article.getCreatedAt());
        
        if (article.getLinkedProduct() != null) {
            dto.setLinkedProductId(article.getLinkedProduct().getId());
            dto.setLinkedProductName(article.getLinkedProduct().getName());
        }
        
        // Convert images
        List<ArticleImage> images = articleImageRepository.findByArticleId(article.getId());
        dto.setImages(images.stream()
                .map(img -> new ArticleDetailDTO.ArticleImageDTO(
                        img.getId(), img.getUrl(), img.getCaption()))
                .collect(Collectors.toList()));
        
        return dto;
    }
}

