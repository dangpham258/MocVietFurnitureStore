package mocviet.repository;

import mocviet.entity.DeliveryTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryTeamRepository extends JpaRepository<DeliveryTeam, Integer> {
    Optional<DeliveryTeam> findByUserId(Integer userId);
    
    @EntityGraph(attributePaths = {"user", "deliveryTeamZones", "deliveryTeamZones.zone"})
    @Override
    java.util.List<DeliveryTeam> findAll();
}

