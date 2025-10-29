package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "DeliveryHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_delivery_id", nullable = false)
    private OrderDelivery orderDelivery;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderDelivery.DeliveryStatus status;
    
    @Column(name = "note", length = 500)
    private String note;
    
    @Column(name = "photo_url", length = 255)
    private String photoUrl;
    
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
    
    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
