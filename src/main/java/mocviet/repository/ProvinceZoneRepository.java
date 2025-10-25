package mocviet.repository;

import mocviet.entity.ProvinceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    
    Optional<ProvinceZone> findByProvinceName(String provinceName);
    
    List<ProvinceZone> findAllByOrderByProvinceNameAsc();
}