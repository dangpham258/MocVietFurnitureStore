package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.ShippingFee;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    Optional<ShippingFee> findByZoneId(Integer zoneId);
}

