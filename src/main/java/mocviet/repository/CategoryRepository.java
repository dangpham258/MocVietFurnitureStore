package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

