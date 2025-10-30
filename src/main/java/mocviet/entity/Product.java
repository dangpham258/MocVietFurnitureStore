package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalDateTime;
import java.util.List; // Import List

@Entity
@Table(name = "Product") // Sửa tên bảng nếu cần
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 160, nullable = false)
    private String name;

    @Column(name = "slug", length = 180, nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)") // Sửa kiểu dữ liệu nếu cần
    private String description;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "sold_qty", nullable = false)
    private Integer soldQty = 0;

    @Column(name = "avg_rating", precision = 2, scale = 1) // Định nghĩa precision và scale
    private BigDecimal avgRating; // Sử dụng BigDecimal

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY) // Thường là LAZY để tối ưu
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Sửa tên cột nếu cần

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id") // collection_id có thể null
    private Category collection; // Sửa tên cột nếu cần

    // ===== THÊM MỐI QUAN HỆ OneToMany VÀO ĐÂY =====
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductImage> productImages;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductVariant> variants;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Wishlist> wishlists;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Viewed> viewedBy;
    
    @OneToMany(mappedBy = "linkedProduct", fetch = FetchType.LAZY)
    private List<Article> linkedArticles;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
