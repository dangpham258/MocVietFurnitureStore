package mocviet.repository;

import mocviet.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Thêm import này

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    // Thêm phương thức này
    List<Banner> findByIsActiveTrueOrderByCreatedAtDesc();
}