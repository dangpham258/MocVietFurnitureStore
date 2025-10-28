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
     * Tìm tất cả notifications của user, sắp xếp theo thời gian tạo mới nhất
     */
    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Tìm notifications chưa đọc của user, sắp xếp từ cũ đến mới
     */
    List<UserNotification> findByUserAndIsReadFalseOrderByCreatedAtAsc(User user);
    
    /**
     * Tìm notifications chưa đọc của user, sắp xếp từ mới đến cũ
     * Lấy thông báo chưa đọc của user
     */
    List<UserNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    /**
     * Đếm số notifications chưa đọc của user
     */
    Long countByUserAndIsReadFalse(User user);
    
    /**
     * Tìm notifications theo title (để kiểm tra trùng lặp)
     */
    List<UserNotification> findByUserAndTitleAndIsReadFalseOrderByCreatedAtDesc(User user, String title);
    
    /**
     * Đánh dấu tất cả notifications là đã đọc
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user = :user")
    void markAllAsRead(@Param("user") User user);
    
    /**
     * Đánh dấu notification là đã đọc
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Integer id);

     /* Lấy N thông báo gần nhất của user (cả đã đọc và chưa đọc)
     */
    List<UserNotification> findTop10ByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Lấy thông báo theo user với phân trang
     */
    Page<UserNotification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Modifying
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);
    
    /**
     * Xóa thông báo cũ hơn N ngày
     */
    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}

