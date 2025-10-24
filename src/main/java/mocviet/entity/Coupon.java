package mocviet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Coupon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    
    @Id
    @Column(name = "code", nullable = false, length = 50)
    private String code;
    
    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    @DecimalMin(value = "0.01", message = "Discount percent must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount percent must be at most 100")
    private BigDecimal discountPercent;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "min_order_amount", nullable = false, precision = 15, scale = 0)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders;
}
