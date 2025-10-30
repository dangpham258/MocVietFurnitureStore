package mocviet.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.DeliveryTeam;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTeamRepository extends JpaRepository<DeliveryTeam, Integer> {
    
    @Query("SELECT dt FROM DeliveryTeam dt JOIN dt.deliveryTeamZones dtz WHERE dtz.zone.id = :zoneId AND dt.isActive = true")
    List<DeliveryTeam> findByZoneAndActive(@Param("zoneId") Integer zoneId);
    
    List<DeliveryTeam> findByIsActiveTrue();
    
    /**
     * Tìm DeliveryTeam theo user ID
     */
    Optional<DeliveryTeam> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"user", "deliveryTeamZones", "deliveryTeamZones.zone"})
    @Override
    java.util.List<DeliveryTeam> findAll();
}
