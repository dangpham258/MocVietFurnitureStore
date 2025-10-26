package mocviet.repository;

import mocviet.entity.Color;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    
    Optional<Color> findByName(String name);
    Optional<Color> findBySlug(String slug);
    
    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Color> findAll();
    
    @Override
    @EntityGraph(attributePaths = {"images"})
    Optional<Color> findById(Integer id);
}

