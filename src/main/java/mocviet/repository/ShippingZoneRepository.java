package mocviet.repository;

import mocviet.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Integer> {
    
    List<ShippingZone> findAll();
}
