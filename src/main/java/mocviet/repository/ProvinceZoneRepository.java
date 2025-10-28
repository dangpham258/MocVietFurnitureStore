package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.ProvinceZone;

@Repository
public interface ProvinceZoneRepository extends JpaRepository<ProvinceZone, Integer> {
    Optional<ProvinceZone> findByProvinceName(String provinceName);
    List<ProvinceZone> findByZoneId(Integer zoneId);
}

