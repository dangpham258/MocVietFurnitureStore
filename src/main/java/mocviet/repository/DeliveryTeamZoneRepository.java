package mocviet.repository;

import mocviet.entity.DeliveryTeamZone;
import mocviet.entity.DeliveryTeam;
import mocviet.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryTeamZoneRepository extends JpaRepository<DeliveryTeamZone, Integer> {
    
    boolean existsByDeliveryTeamAndZone(DeliveryTeam deliveryTeam, ShippingZone zone);
    
    @Query("SELECT COUNT(dtz) FROM DeliveryTeamZone dtz WHERE dtz.zone = :zone")
    int countByZone(@Param("zone") ShippingZone zone);
    
    @Query("SELECT COUNT(dtz) > 0 FROM DeliveryTeamZone dtz WHERE dtz.zone = :zone AND dtz.deliveryTeam.isActive = :isActive")
    boolean existsByZoneAndDeliveryTeamIsActive(@Param("zone") ShippingZone zone, @Param("isActive") boolean isActive);
}
