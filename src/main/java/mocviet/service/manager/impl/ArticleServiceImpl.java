package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Article;
import mocviet.entity.Article.ArticleType;
import mocviet.entity.ArticleImage;
import mocviet.entity.Product;
import mocviet.repository.ArticleImageRepository;
import mocviet.repository.ArticleRepository;
import mocviet.repository.ProductRepository;
import mocviet.service.manager.IArticleService;
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
public class ArticleServiceImpl implements IArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Article createArticle(CreateArticleRequest request, String authorUsername) {
        ArticleType articleType;
        try {
            articleType = ArticleType.valueOf(request.getArticleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ. Chỉ chấp nhận: MEDIA, NEWS, PEOPLE");
        }

        Product linkedProduct = null;
        if (request.getLinkedProductId() != null) {
            linkedProduct = productRepository.findById(request.getLinkedProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm liên quan không tồn tại"));
        }

        String slug = ensureUniqueSlug(generateSlug(request.getTitle()));

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

        if (article.getStatus()) {
            article.setPublishedAt(LocalDateTime.now());
        }

        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article updateArticle(UpdateArticleRequest request, String authorUsername) {
        Article article = articleRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));

        if (!article.getAuthor().equals(authorUsername)) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        ArticleType articleType;
        try {
            articleType = ArticleType.valueOf(request.getArticleType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Loại bài viết không hợp lệ. Chỉ chấp nhận: MEDIA, NEWS, PEOPLE");
        }

        Product linkedProduct = null;
        if (request.getLinkedProductId() != null) {
            linkedProduct = productRepository.findById(request.getLinkedProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm liên quan không tồn tại"));
        }

        String newSlug = article.getSlug();
        if (!article.getTitle().equals(request.getTitle())) {
            newSlug = ensureUniqueSlugExcept(generateSlug(request.getTitle()), article.getId());
        }

        Boolean oldStatus = article.getStatus();

        article.setTitle(request.getTitle());
        article.setSlug(newSlug);
        article.setArticleType(articleType);
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        article.setStatus(request.getStatus() != null ? request.getStatus() : false);
        article.setLinkedProduct(linkedProduct);

        if (!oldStatus && article.getStatus() && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }

        return articleRepository.save(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleListDTO> getArticlesByAuthor(String authorUsername, String articleType, Boolean status, String keyword, Pageable pageable) {
        ArticleType type = null;
        if (articleType != null && !articleType.isEmpty()) {
            try {
                type = ArticleType.valueOf(articleType.toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        Page<Article> articles = articleRepository.findByAuthorWithFilters(
                authorUsername, type, status, keyword, pageable);
        return articles.map(this::convertToListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailDTO getArticleDetail(Integer id, String authorUsername) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bài viết không tồn tại"));
        if (!article.getAuthor().equals(authorUsername)) {
            throw new IllegalArgumentException("Bạn không có quyền xem bài viết này");
        }
        return convertToDetailDTO(article);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(Product::getIsActive)
                .collect(Collectors.toList());
    }

    // ==== helpers ====
    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled";
        }
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutDiacritics = pattern.matcher(normalized).replaceAll("");
        String slug = withoutDiacritics.toLowerCase(Locale.ROOT)
                .replace('đ', 'd')
                .replace('Đ', 'd');
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        if (slug.length() > 290) {
            slug = slug.substring(0, 290).replaceAll("-+$", "");
        }
        return slug.isEmpty() ? "untitled" : slug;
    }

    private String ensureUniqueSlug(String slug) {
        String originalSlug = slug;
        int counter = 1;
        while (articleRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    private String ensureUniqueSlugExcept(String slug, Integer articleId) {
        Optional<Article> existing = articleRepository.findBySlug(slug);
        if (existing.isEmpty() || existing.get().getId().equals(articleId)) return slug;
        String originalSlug = slug;
        int counter = 1;
        while (true) {
            slug = originalSlug + "-" + counter;
            existing = articleRepository.findBySlug(slug);
            if (existing.isEmpty() || existing.get().getId().equals(articleId)) return slug;
            counter++;
        }
    }

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
        List<ArticleImage> images = articleImageRepository.findByArticleId(article.getId());
        dto.setImages(images.stream()
                .map(img -> new ArticleDetailDTO.ArticleImageDTO(img.getId(), img.getUrl(), img.getCaption()))
                .collect(Collectors.toList()));
        if (article.getLinkedProduct() != null) {
            dto.setLinkedProductId(article.getLinkedProduct().getId());
            dto.setLinkedProductName(article.getLinkedProduct().getName());
            dto.setLinkedProductSlug(article.getLinkedProduct().getSlug());
        }
        return dto;
    }
}


