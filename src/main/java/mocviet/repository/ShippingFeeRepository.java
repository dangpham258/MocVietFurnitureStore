package mocviet.repository;

import mocviet.entity.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    Optional<ShippingFee> findByZoneId(Integer zoneId);
}

