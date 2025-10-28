package mocviet.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
