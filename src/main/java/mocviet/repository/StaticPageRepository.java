package mocviet.repository;

import mocviet.entity.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Integer> {
    
    boolean existsBySlugIgnoreCase(String slug);
    
    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Integer id);
    
    Optional<StaticPage> findBySlugIgnoreCase(String slug);
}
