package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "OrderDelivery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDelivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_team_id", nullable = false)
    private DeliveryTeam deliveryTeam;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeliveryStatus status = DeliveryStatus.RECEIVED;
    
    @Column(name = "proof_image_url", length = 255)
    private String proofImageUrl;
    
    @Column(name = "note", length = 500)
    private String note;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum DeliveryStatus {
        RECEIVED, IN_TRANSIT, DONE, RETURN_PICKUP
    }
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
