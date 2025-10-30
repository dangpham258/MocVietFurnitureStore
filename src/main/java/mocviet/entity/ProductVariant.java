package mocviet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ProductVariant", 
       uniqueConstraints = @UniqueConstraint(name = "UQ_PV_Combo", columnNames = {"product_id", "color_id", "type_name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;
    
    @Column(name = "type_name", nullable = false, length = 80)
    private String typeName;
    
    @Column(name = "sku", nullable = false, unique = true, length = 80)
    private String sku;
    
    @Column(name = "price", nullable = false, precision = 15, scale = 0)
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private BigDecimal price;
    
    @Column(name = "discount_percent", nullable = false)
    @Min(value = 0, message = "Discount percent must be at least 0")
    @Max(value = 100, message = "Discount percent must be at most 100")
    private Integer discountPercent = 0;
    
    @Column(name = "stock_qty", nullable = false)
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQty = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", length = 20)
    private PromotionType promotionType;
    
    public enum PromotionType {
        SALE, OUTLET
    }
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Computed column for sale_price (calculated by database)
    @Column(name = "sale_price", precision = 15, scale = 0, insertable = false, updatable = false)
    private BigDecimal salePrice;
    
    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}
