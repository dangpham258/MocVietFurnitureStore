package mocviet.repository;

import mocviet.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    
    Optional<Color> findBySlug(String slug);
    
    Boolean existsByName(String name);
    
    Boolean existsBySlug(String slug);
    
    Boolean existsByNameAndIdNot(String name, Integer id);
    
    Boolean existsBySlugAndIdNot(String slug, Integer id);
    
    @Query("SELECT c FROM Color c WHERE c.isActive = true ORDER BY c.name")
    List<Color> findActiveColors();
    
    List<Color> findByIsActiveTrue();
}
