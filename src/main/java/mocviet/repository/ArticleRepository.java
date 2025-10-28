package mocviet.repository;

import mocviet.entity.Article;
import mocviet.entity.Article.ArticleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    
    /**
     * Tìm bài viết theo slug (unique)
     */
    Optional<Article> findBySlug(String slug);
    
    /**
     * Kiểm tra slug đã tồn tại
     */
    boolean existsBySlug(String slug);
    
    /**
     * Tìm bài viết theo tác giả và trạng thái (cho manager xem bài viết của mình)
     */
    Page<Article> findByAuthor(String author, Pageable pageable);
    
    /**
     * Tìm bài viết theo tác giả, loại và trạng thái
     */
    Page<Article> findByAuthorAndArticleType(String author, ArticleType articleType, Pageable pageable);
    
    /**
     * Tìm bài viết theo tác giả và status
     */
    Page<Article> findByAuthorAndStatus(String author, Boolean status, Pageable pageable);
    
    /**
     * Tìm bài viết theo tác giả với từ khóa tìm kiếm
     */
    @Query("SELECT a FROM Article a WHERE a.author = :author " +
           "AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> findByAuthorAndKeyword(@Param("author") String author, 
                                          @Param("keyword") String keyword, 
                                          Pageable pageable);
    
    /**
     * Tìm bài viết theo tác giả với nhiều điều kiện lọc
     */
    @Query("SELECT a FROM Article a WHERE a.author = :author " +
           "AND (:articleType IS NULL OR a.articleType = :articleType) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> findByAuthorWithFilters(@Param("author") String author,
                                           @Param("articleType") ArticleType articleType,
                                           @Param("status") Boolean status,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);
    
    /**
     * Đếm số bài viết theo tác giả
     */
    long countByAuthor(String author);
    
    /**
     * Đếm số bài viết đã xuất bản theo tác giả
     */
    long countByAuthorAndStatus(String author, Boolean status);
    
    /**
     * Đếm số bài viết nháp theo tác giả
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = :author AND a.status = false")
    long countDraftsByAuthor(@Param("author") String author);
    
    /**
     * Tổng lượt xem của tất cả bài viết theo tác giả
     */
    @Query("SELECT SUM(a.views) FROM Article a WHERE a.author = :author")
    Long sumViewsByAuthor(@Param("author") String author);
}

