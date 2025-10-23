package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductVariant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;
    
    @Column(name = "type_name", nullable = false, length = 80)
    private String typeName;
    
    @Column(name = "sku", nullable = false, length = 80, unique = true)
    private String sku;
    
    @Column(name = "price", nullable = false, precision = 15, scale = 0)
    private BigDecimal price;
    
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent = 0;
    
    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty = 0;
    
    @Column(name = "promotion_type", length = 20)
    private String promotionType;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "sale_price", precision = 15, scale = 0)
    private BigDecimal salePrice;
}
