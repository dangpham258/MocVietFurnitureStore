package mocviet.repository;

import mocviet.entity.ShippingZone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Integer> {
    Optional<ShippingZone> findBySlug(String slug);
    
    @EntityGraph(attributePaths = {"shippingFee"})
    @Override
    java.util.List<ShippingZone> findAll();
}

