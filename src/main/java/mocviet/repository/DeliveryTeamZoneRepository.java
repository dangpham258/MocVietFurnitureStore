package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.DeliveryTeamZone;

@Repository
public interface DeliveryTeamZoneRepository extends JpaRepository<DeliveryTeamZone, Integer> {
    List<DeliveryTeamZone> findByDeliveryTeamId(Integer teamId);

    Optional<DeliveryTeamZone> findByDeliveryTeamIdAndZoneId(Integer teamId, Integer zoneId);
}

