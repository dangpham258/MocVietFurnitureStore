package mocviet.repository;

import mocviet.entity.Conversation;
import mocviet.entity.User; // Import User nếu cần tìm theo User ID
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    // Tìm conversation của guest theo email và còn đang mở (nếu cần)
    Optional<Conversation> findByGuestEmailAndStatus(String guestEmail, Conversation.ConversationStatus status);

    // Trả về tất cả conversations theo email và status, sắp xếp mới nhất trước (tránh NonUniqueResult)
    List<Conversation> findAllByGuestEmailAndStatusOrderByCreatedAtDesc(
        String guestEmail,
        Conversation.ConversationStatus status
    );

    // Tìm tất cả OPEN của user ngoại trừ 1 id (để đóng bớt)
    List<Conversation> findAllByUserAndStatusAndIdNotOrderByCreatedAtDesc(
        User user,
        Conversation.ConversationStatus status,
        Integer excludeId
    );

    // Tìm tất cả OPEN theo email ngoại trừ 1 id (để đóng bớt)
    List<Conversation> findAllByGuestEmailAndStatusAndIdNotOrderByCreatedAtDesc(
        String guestEmail,
        Conversation.ConversationStatus status,
        Integer excludeId
    );

    // Tìm conversation của customer (có thể có nhiều) → dùng list để tránh NonUniqueResult
    List<Conversation> findAllByUserAndStatusOrderByCreatedAtDesc(User user, Conversation.ConversationStatus status);

    // Lấy danh sách conversation đang mở (cho manager)
    List<Conversation> findByStatusOrderByCreatedAtDesc(Conversation.ConversationStatus status);
}