package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import mocviet.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);
    Optional<Category> findBySlug(String slug);

    List<Category> findByParentId(Integer parentId);
    List<Category> findByType(Category.CategoryType type);
    List<Category> findByParentIdAndType(Integer parentId, Category.CategoryType type);

    @Override
    @EntityGraph(attributePaths = {"products", "parent"})
    List<Category> findAll();

    @Override
    @EntityGraph(attributePaths = {"products", "parent"})
    Optional<Category> findById(Integer id);
    
    Boolean existsByName(String name);
    
    Boolean existsBySlug(String slug);
    
    Boolean existsByNameAndIdNot(String name, Integer id);
    
    Boolean existsBySlugAndIdNot(String slug, Integer id);
    
    @Query("SELECT c FROM Category c WHERE c.type = :type AND c.isActive = true")
    List<Category> findByTypeAndActive(@Param("type") Category.CategoryType type);
    
    List<Category> findByTypeAndIsActiveTrue(Category.CategoryType type);
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent IS NULL AND c.isActive = true")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent.id = :parentId AND c.isActive = true")
    List<Category> findSubCategories(@Param("parentId") Integer parentId);
    
    @Query("SELECT c FROM Category c WHERE c.type = 'COLLECTION' AND c.isActive = true")
    List<Category> findActiveCollections();
    
    @Query("SELECT c FROM Category c WHERE c.type = 'CATEGORY' AND c.parent IS NOT NULL AND c.isActive = true")
    List<Category> findLeafCategories();

    // Tìm category theo slug và đang active
    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    // Lấy danh sách con trực tiếp đang active
    List<Category> findByParentIdAndIsActiveTrue(Integer parentId);

    // Lấy tất cả category đang active (để xây dựng cây) - Tùy chọn, có thể không cần ngay
    // Sửa ORDER BY để xử lý parent null với NULLS FIRST (tùy DB) hoặc CASE
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.isActive = true ORDER BY CASE WHEN c.parent.id IS NULL THEN 0 ELSE 1 END, c.parent.id ASC, c.name ASC")
    List<Category> findAllActiveWithParent();
}

