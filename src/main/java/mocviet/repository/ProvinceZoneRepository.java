package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.ProvinceZone;

import mocviet.entity.ProvinceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    Optional<ProvinceZone> findByProvinceName(String provinceName);
    List<ProvinceZone> findByZoneId(Integer zoneId);
}


@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    
    Optional<ProvinceZone> findByProvinceName(String provinceName);
    
    /**
     * Tìm zone ID theo province name
     */
    @Query("SELECT pz.zone.id FROM ProvinceZone pz WHERE pz.provinceName = :provinceName")
    Optional<Integer> findZoneIdByProvinceName(@Param("provinceName") String provinceName);
    
    List<ProvinceZone> findAllByOrderByProvinceNameAsc();
}
