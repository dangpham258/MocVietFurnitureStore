package mocviet.repository;

import mocviet.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleImageRepository extends JpaRepository<ArticleImage, Integer> {
    
    /**
     * Tìm tất cả ảnh của một bài viết
     */
    List<ArticleImage> findByArticleId(Integer articleId);
    
    /**
     * Xóa tất cả ảnh của một bài viết
     */
    @Modifying
    @Query("DELETE FROM ArticleImage ai WHERE ai.article.id = :articleId")
    void deleteByArticleId(@Param("articleId") Integer articleId);
    
    /**
     * Đếm số lượng ảnh của một bài viết
     */
    long countByArticleId(Integer articleId);
}

