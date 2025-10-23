package mocviet.repository;

import mocviet.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findBySlug(String slug);
    
    Boolean existsByName(String name);
    
    Boolean existsBySlug(String slug);
    
    Boolean existsByNameAndIdNot(String name, Integer id);
    
    Boolean existsBySlugAndIdNot(String slug, Integer id);
    
    @Query("SELECT c FROM Category c WHERE c.type = :type AND c.isActive = true")
    List<Category> findByTypeAndActive(@Param("type") String type);
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent IS NULL AND c.isActive = true")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent.id = :parentId AND c.isActive = true")
    List<Category> findSubCategories(@Param("parentId") Integer parentId);
    
    @Query("SELECT c FROM Category c WHERE c.type = 'COLLECTION' AND c.isActive = true")
    List<Category> findActiveCollections();
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent IS NULL AND c.isActive = true")
    List<Category> findLeafCategories();
}
