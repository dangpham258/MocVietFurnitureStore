package mocviet.repository;

import mocviet.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Thêm import
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    Optional<Article> findBySlugAndStatusTrue(String slug);

    // Sử dụng @Query tường minh, CÓ ORDER BY
    @Query("SELECT a FROM Article a WHERE a.status = true ORDER BY a.publishedAt DESC, a.id ASC")
    Page<Article> findByStatusTrue(Pageable pageable); // Tên không có OrderBy

    // Sử dụng @Query tường minh, CÓ ORDER BY
    @Query("SELECT a FROM Article a WHERE a.articleType = :articleType AND a.status = true ORDER BY a.publishedAt DESC, a.id ASC")
    Page<Article> findByArticleTypeAndStatusTrue(@Param("articleType") Article.ArticleType articleType, Pageable pageable); // Tên không có OrderBy

    @Query("SELECT DISTINCT a.articleType FROM Article a WHERE a.status = true")
    List<Article.ArticleType> findDistinctArticleTypes();
}