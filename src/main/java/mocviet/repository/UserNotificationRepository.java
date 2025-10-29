package mocviet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mocviet.entity.User;
import mocviet.entity.UserNotification;

import mocviet.entity.User;
import mocviet.entity.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);

    List<UserNotification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead);

    long countByUser(User user);

    long countByUserAndIsRead(User user, Boolean isRead);

    Optional<UserNotification> findByIdAndUser(Integer id, User user);
}



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
     */
    List<UserNotification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    /**
     * Tìm notifications chưa đọc theo userId (ASC)
     */
    List<UserNotification> findByUserIdAndIsReadFalseOrderByCreatedAtAsc(Integer userId);
    
    /**
     * Tìm notifications chưa đọc theo userId (DESC)
     */
    @Query("SELECT n FROM UserNotification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<UserNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(@Param("userId") Integer userId);
    
    /**
     * Đếm số notifications chưa đọc của user
     */
    Long countByUserAndIsReadFalse(User user);
    
    /**
     * Đếm số notifications chưa đọc theo userId
     */
    long countByUserIdAndIsReadFalse(Integer userId);
    
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
     * Đánh dấu tất cả thông báo chưa đọc của user là đã đọc theo userId
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);
    
    /**
     * Đánh dấu notification là đã đọc
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification n SET n.isRead = true WHERE n.id = :id")
    void markAsRead(@Param("id") Integer id);

    /**
     * Lấy 10 thông báo gần nhất của user (cả đã đọc và chưa đọc)
     */
    List<UserNotification> findTop10ByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Lấy thông báo theo user với phân trang
     */
    Page<UserNotification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    /**
     * Lấy thông báo theo userId với phân trang
     */
    @Query("SELECT n FROM UserNotification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<UserNotification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
    
    /**
     * Tìm một thông báo cụ thể của một user theo ID
     */
    Optional<UserNotification> findByIdAndUserId(Integer id, Integer userId);
    
    /**
     * Xóa thông báo cũ hơn N ngày
     */
    @Modifying
    @Query("DELETE FROM UserNotification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Tìm thông báo theo ID và user ID để xóa
     */
    void deleteByIdAndUserId(Integer id, Integer userId);
}
