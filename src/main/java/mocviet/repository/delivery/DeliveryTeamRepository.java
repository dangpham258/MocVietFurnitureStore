package mocviet.repository.delivery;

import mocviet.entity.DeliveryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryTeamRepository extends JpaRepository<DeliveryTeam, Integer> {

    /**
     * Tìm DeliveryTeam theo user ID
     */
    Optional<DeliveryTeam> findByUserId(Integer userId);
}