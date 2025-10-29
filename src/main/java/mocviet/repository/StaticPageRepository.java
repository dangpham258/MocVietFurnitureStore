package mocviet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.StaticPage;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Integer> {

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Integer id);

    Optional<StaticPage> findBySlugIgnoreCase(String slug);

    Optional<StaticPage> findBySlugAndIsActiveTrue(String slug);
}
