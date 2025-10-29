package mocviet.repository;

import mocviet.entity.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Integer> {

    /**
     * Tìm một trang tĩnh theo slug và đảm bảo trang đó đang hoạt động (isActive = true)
     * @param slug Slug của trang cần tìm
     * @return Optional chứa StaticPage nếu tìm thấy
     */
    Optional<StaticPage> findBySlugAndIsActiveTrue(String slug);

}