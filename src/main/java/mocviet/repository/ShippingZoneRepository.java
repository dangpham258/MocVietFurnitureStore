package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.ShippingZone;

import mocviet.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Integer> {
    Optional<ShippingZone> findBySlug(String slug);

    @EntityGraph(attributePaths = {"shippingFee"})
    @Override
    java.util.List<ShippingZone> findAll();
}

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Integer> {
    
    List<ShippingZone> findAll();
}
