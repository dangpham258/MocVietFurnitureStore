package mocviet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ShippingZone")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingZone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;
    
    @OneToOne(mappedBy = "zone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ShippingFee shippingFee;
    
    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProvinceZone> provinceZones;
    
    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DeliveryTeamZone> deliveryTeamZones;
}
