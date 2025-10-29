package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ArticleImage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
    
    @Column(name = "url", nullable = false, length = 255)
    private String url;
    
    @Column(name = "caption", length = 255)
    private String caption;
}
