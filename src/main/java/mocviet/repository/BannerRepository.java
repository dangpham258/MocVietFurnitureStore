package mocviet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List; // Thêm import này

import mocviet.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {

    List<Banner> findByIsActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT COUNT(b) FROM Banner b")
    long countAllBanners();
}
