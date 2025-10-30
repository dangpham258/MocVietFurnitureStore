package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @Column(name = "name", nullable = false, length = 120)
    private String name;
    
    @Column(name = "slug", nullable = false, unique = true, length = 160)
    private String slug;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CategoryType type;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum CategoryType {
        CATEGORY, COLLECTION
    }
    
    // Quan hệ phân cấp: Category cha có nhiều Category con
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children;
    
    // Quan hệ với Product: Category có nhiều Product thuộc về
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;
    
    // Quan hệ với Product: Collection có nhiều Product thuộc về
    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    private List<Product> collectionProducts;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
