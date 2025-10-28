package mocviet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mocviet.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {

    @Query("SELECT COUNT(b) FROM Banner b")
    long countAllBanners();
}