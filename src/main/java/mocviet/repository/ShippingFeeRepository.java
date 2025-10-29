package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.ShippingFee;

import mocviet.entity.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    Optional<ShippingFee> findByZoneId(Integer zoneId);
}


@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, Integer> {
    
    /**
     * Lấy phí vận chuyển theo zone ID
     */
    @Query("SELECT sf FROM ShippingFee sf JOIN sf.zone z WHERE z.id = :zoneId")
    Optional<ShippingFee> findByZoneId(@Param("zoneId") Integer zoneId);
    
    /**
     * Lấy phí vận chuyển theo province name
     */
    @Query("SELECT sf FROM ShippingFee sf JOIN sf.zone z JOIN ProvinceZone pz ON pz.zone.id = z.id " +
           "WHERE pz.provinceName = :provinceName")
    Optional<ShippingFee> findByProvinceName(@Param("provinceName") String provinceName);
}

