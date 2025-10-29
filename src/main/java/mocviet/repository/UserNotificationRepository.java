package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mocviet.entity.UserNotification;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    /**
     * Tìm các thông báo chưa đọc của một user, sắp xếp mới nhất trước.
     */
    @Query("SELECT n FROM UserNotification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<UserNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(@Param("userId") Integer userId);

    /**
     * Tìm một thông báo cụ thể của một user theo ID.
     */
    Optional<UserNotification> findByIdAndUserId(Integer id, Integer userId);

    /**
     * Đánh dấu tất cả thông báo chưa đọc của user thành đã đọc.
     * @return Số lượng bản ghi đã được cập nhật.
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);

    /**
     * Tìm tất cả thông báo của một user, sắp xếp mới nhất trước.
     */
    @Query("SELECT n FROM UserNotification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<UserNotification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);

    /**
     * Đếm số thông báo chưa đọc của một user.
     */
    long countByUserIdAndIsReadFalse(Integer userId);

    /**
     * Tìm thông báo theo ID và user ID để xóa.
     */
    void deleteByIdAndUserId(Integer id, Integer userId);
}