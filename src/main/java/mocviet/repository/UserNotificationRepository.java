package mocviet.repository;

import mocviet.entity.User;
import mocviet.entity.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    
    /**
     * Lấy thông báo chưa đọc của user
     */
    List<UserNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    /**
     * Lấy N thông báo gần nhất của user (cả đã đọc và chưa đọc)
     */
    List<UserNotification> findTop10ByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Lấy thông báo theo user với phân trang
     */
    Page<UserNotification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    /**
     * Đếm số thông báo chưa đọc của user
     */
    long countByUserAndIsReadFalse(User user);
    
    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);
    
    /**
     * Đánh dấu một thông báo là đã đọc
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.id = :notificationId")
    int markAsRead(@Param("notificationId") Integer notificationId);
    
    /**
     * Xóa thông báo cũ hơn N ngày
     */
    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}

