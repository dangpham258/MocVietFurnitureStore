package mocviet.repository;

import mocviet.entity.DeliveryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTeamRepository extends JpaRepository<DeliveryTeam, Integer> {
    
    @Query("SELECT dt FROM DeliveryTeam dt JOIN dt.deliveryTeamZones dtz WHERE dtz.zone.id = :zoneId AND dt.isActive = true")
    List<DeliveryTeam> findByZoneAndActive(@Param("zoneId") Integer zoneId);
    
    List<DeliveryTeam> findByIsActiveTrue();
}
