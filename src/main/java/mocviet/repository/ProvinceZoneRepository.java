package mocviet.repository;

import mocviet.entity.ProvinceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    
    List<ProvinceZone> findAllByOrderByProvinceNameAsc();
    
    /**
     * Tìm zone ID theo province name
     */
    @Query("SELECT pz.zone.id FROM ProvinceZone pz WHERE pz.provinceName = :provinceName")
    Optional<Integer> findZoneIdByProvinceName(@Param("provinceName") String provinceName);
    
}
