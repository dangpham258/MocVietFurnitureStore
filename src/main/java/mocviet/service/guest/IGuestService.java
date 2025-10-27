package mocviet.service.guest;

import mocviet.dto.ArticleSummaryDTO;
import mocviet.dto.GuestMessageRequestDTO;
import mocviet.dto.MessageResponse;
import mocviet.dto.ProductCardDTO;
import mocviet.entity.Article;
import mocviet.entity.Banner;
import mocviet.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import mocviet.entity.StaticPage; // Thêm import
import java.util.Optional;     // Thêm import

import java.util.List;

public interface IGuestService {

    /** Lấy danh sách banner đang hoạt động */
    List<Banner> getActiveBanners();

    /** Lấy top sản phẩm nổi bật (theo lượt xem) */
    List<ProductCardDTO> getFeaturedProducts(int limit);

    /** Lấy top sản phẩm mới nhất (theo ngày tạo) */
    List<ProductCardDTO> getNewestProducts(int limit);

    /** Lấy top sản phẩm khuyến mãi (theo % giảm giá cao nhất) */
    List<ProductCardDTO> getTopDiscountedProducts(int limit);

    /** Lấy top sản phẩm bán chạy (theo số lượng đã bán) */
    List<ProductCardDTO> getBestSellingProducts(int limit);

    /** Lấy top đánh giá tốt nhất (5 sao, sản phẩm khác nhau, mới nhất) */
    List<Review> getBestReviews(int limit);

    /** Lấy danh sách bài viết theo loại với phân trang */
    Page<ArticleSummaryDTO> findArticlesByType(Article.ArticleType type, Pageable pageable);

    /** Lấy tất cả bài viết với phân trang */
    Page<ArticleSummaryDTO> findAllArticles(Pageable pageable);

    /** Xử lý tin nhắn gửi từ Guest */
    MessageResponse handleGuestMessage(GuestMessageRequestDTO messageRequest);
    Optional<StaticPage> getStaticPageBySlug(String slug);
}