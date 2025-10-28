package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.DeliveryTeam;

@Repository
public interface DeliveryTeamRepository extends JpaRepository<DeliveryTeam, Integer> {
    Optional<DeliveryTeam> findByUserId(Integer userId);

    @EntityGraph(attributePaths = {"user", "deliveryTeamZones", "deliveryTeamZones.zone"})
    @Override
    java.util.List<DeliveryTeam> findAll();
}

