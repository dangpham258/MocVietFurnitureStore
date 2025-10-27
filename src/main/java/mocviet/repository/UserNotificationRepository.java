package mocviet.repository;

import mocviet.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    /**
     * Tìm các thông báo chưa đọc của một user, sắp xếp mới nhất trước.
     */
    List<UserNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);

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
}