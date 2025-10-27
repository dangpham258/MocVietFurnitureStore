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
    private BigDecimal price;
    
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent = 0;
    
    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty = 0;
    
    @Column(name = "promotion_type", length = 20)
    private String promotionType;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Computed column for sale_price
    @Column(name = "sale_price", precision = 15, scale = 0)
    private BigDecimal salePrice;
    
    @PostLoad
    @PostPersist
    @PostUpdate
    private void calculateSalePrice() {
        if (price != null && discountPercent != null) {
            if (discountPercent == 0) {
                salePrice = price;
            } else {
                BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discountPercent))
                        .divide(BigDecimal.valueOf(100));
                salePrice = price.subtract(discountAmount);
                // Làm tròn về bậc nghìn
                salePrice = salePrice.divide(BigDecimal.valueOf(1000), 0, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(1000));
            }
        }
    }
}
