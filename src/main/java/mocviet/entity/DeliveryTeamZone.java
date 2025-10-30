package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DeliveryTeamZone", 
       uniqueConstraints = @UniqueConstraint(name = "UQ_DTZ", columnNames = {"delivery_team_id", "zone_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTeamZone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_team_id", nullable = false)
    private DeliveryTeam deliveryTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ShippingZone zone;
}
