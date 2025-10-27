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

    // Tìm category theo slug và đang active
    Optional<Category> findBySlugAndIsActiveTrue(String slug);

    // Lấy danh sách con trực tiếp đang active
    List<Category> findByParentIdAndIsActiveTrue(Integer parentId);

    // Lấy tất cả category đang active (để xây dựng cây) - Tùy chọn, có thể không cần ngay
    // Sửa ORDER BY để xử lý parent null với NULLS FIRST (tùy DB) hoặc CASE
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.isActive = true ORDER BY CASE WHEN c.parent.id IS NULL THEN 0 ELSE 1 END, c.parent.id ASC, c.name ASC")
    List<Category> findAllActiveWithParent();
}