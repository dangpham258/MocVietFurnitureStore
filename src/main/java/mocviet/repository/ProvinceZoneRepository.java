package mocviet.repository;

import mocviet.entity.ProvinceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    
    List<ProvinceZone> findAllByOrderByProvinceNameAsc();
    
}
