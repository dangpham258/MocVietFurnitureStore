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

    // Tìm conversation của customer (nếu cần sau này)
    Optional<Conversation> findByUserAndStatus(User user, Conversation.ConversationStatus status);

    // Lấy danh sách conversation đang mở (cho manager)
    List<Conversation> findByStatusOrderByCreatedAtDesc(Conversation.ConversationStatus status);
}