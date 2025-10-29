package mocviet.repository;

import mocviet.entity.Conversation;
import mocviet.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Lấy tin nhắn theo conversation, sắp xếp theo thời gian tạo
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    // Lấy tin nhắn theo ID conversation
    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
}