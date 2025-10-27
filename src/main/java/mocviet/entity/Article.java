package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "title", nullable = false, length = 300)
    private String title;
    
    @Column(name = "slug", nullable = false, unique = true, length = 300)
    private String slug;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "article_type", nullable = false, length = 20)
    private ArticleType articleType = ArticleType.NEWS;
    
    @Column(name = "summary", length = 500)
    private String summary;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "thumbnail", length = 255)
    private String thumbnail;
    
    @Column(name = "author", length = 100)
    private String author;
    
    @Column(name = "views", nullable = false)
    private Integer views = 0;
    
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
    
    @Column(name = "status", nullable = false)
    private Boolean status = true;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_product_id")
    private Product linkedProduct;
    
    public enum ArticleType {
        MEDIA, NEWS, PEOPLE
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
