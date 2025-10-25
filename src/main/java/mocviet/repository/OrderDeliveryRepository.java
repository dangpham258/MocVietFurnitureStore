package mocviet.repository;

import mocviet.entity.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Integer> {
    
    @Query("SELECT COUNT(od) FROM OrderDelivery od WHERE od.deliveryTeam = :team AND od.status IN :statuses")
    int countByDeliveryTeamAndStatusIn(@Param("team") mocviet.entity.DeliveryTeam team, @Param("statuses") List<OrderDelivery.DeliveryStatus> statuses);
    
    long countByStatus(OrderDelivery.DeliveryStatus status);
}
