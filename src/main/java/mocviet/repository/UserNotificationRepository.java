package mocviet.repository;

import mocviet.entity.User;
import mocviet.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    
    List<UserNotification> findByUserOrderByCreatedAtDesc(User user);
    
    List<UserNotification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead);
    
    long countByUser(User user);
    
    long countByUserAndIsRead(User user, Boolean isRead);
    
    Optional<UserNotification> findByIdAndUser(Integer id, User user);
}

