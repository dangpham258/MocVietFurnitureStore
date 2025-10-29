package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mocviet.entity.Color;

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

    Optional<Color> findByName(String name);

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Color> findAll();

    @Override
    @EntityGraph(attributePaths = {"images"})
    Optional<Color> findById(Integer id);
}
