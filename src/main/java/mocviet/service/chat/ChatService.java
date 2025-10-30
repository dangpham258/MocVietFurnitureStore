package mocviet.service.chat;

import mocviet.dto.ChatMessageDTO;
import mocviet.dto.ConversationDTO;
import mocviet.entity.Conversation;
import mocviet.entity.Message;
import mocviet.entity.User;
import mocviet.entity.UserNotification;
import mocviet.repository.ConversationRepository;
import mocviet.repository.MessageRepository;
import mocviet.repository.UserNotificationRepository;
import mocviet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;

    /**
     * Lưu tin nhắn mới vào database
     */
    @Transactional
    public ChatMessageDTO saveMessage(
            Integer conversationId,
            Message.SenderType senderType,
            Integer senderId,
            String guestName,
            String guestEmail,
            String content,
            String attachmentUrl) {

        // Tìm hoặc tạo conversation
        Conversation conversation;
        if (conversationId != null) {
            conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation không tồn tại"));
        } else {
            // Không có conversationId: cố gắng tái sử dụng conversation đang OPEN
            if (senderType == Message.SenderType.CUSTOMER && senderId != null) {
                // Customer: tìm conversation OPEN của user
                User customer = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
                List<Conversation> existingList = conversationRepository
                    .findAllByUserAndStatusOrderByCreatedAtDesc(customer, Conversation.ConversationStatus.OPEN);
                if (!existingList.isEmpty()) {
                    conversation = existingList.get(0);
                } else {
                    // Tạo conversation mới cho customer
                    conversation = new Conversation();
                conversation.setUser(customer);
                    conversation.setStatus(Conversation.ConversationStatus.OPEN);
                    conversation = conversationRepository.save(conversation);
                }
                // Đóng các conversation OPEN khác của user nếu còn tồn tại (giữ 1 hội thoại OPEN duy nhất)
                try {
                    List<Conversation> others = conversationRepository
                        .findAllByUserAndStatusAndIdNotOrderByCreatedAtDesc(
                            customer, Conversation.ConversationStatus.OPEN, conversation.getId());
                    for (Conversation other : others) {
                        other.setStatus(Conversation.ConversationStatus.CLOSED);
                        other.setClosedAt(LocalDateTime.now());
                        conversationRepository.save(other);
                    }
                } catch (Exception ignored) {}
            } else {
                // Guest: tìm conversation OPEN theo email trước khi tạo mới
                if (guestEmail != null && !guestEmail.isBlank()) {
                    List<Conversation> openList = conversationRepository
                        .findAllByGuestEmailAndStatusOrderByCreatedAtDesc(
                            guestEmail,
                            Conversation.ConversationStatus.OPEN
                        );
                    if (!openList.isEmpty()) {
                        conversation = openList.get(0); // lấy conversation mới nhất
                    } else {
                        conversation = new Conversation();
                conversation.setGuestName(guestName);
                conversation.setGuestEmail(guestEmail);
            conversation.setStatus(Conversation.ConversationStatus.OPEN);
            conversation = conversationRepository.save(conversation);
                    }
                    // Đóng các conversation OPEN khác cùng email nếu còn tồn tại
                    try {
                        List<Conversation> others = conversationRepository
                            .findAllByGuestEmailAndStatusAndIdNotOrderByCreatedAtDesc(
                                guestEmail, Conversation.ConversationStatus.OPEN, conversation.getId());
                        for (Conversation other : others) {
                            other.setStatus(Conversation.ConversationStatus.CLOSED);
                            other.setClosedAt(LocalDateTime.now());
                            conversationRepository.save(other);
                        }
                    } catch (Exception ignored) {}
                } else {
                    throw new RuntimeException("Thiếu email của guest để tạo/tìm conversation");
                }
            }
        }

        // Tạo message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderType(senderType);
        
        if (senderId != null) {
            User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender không tồn tại"));
            message.setSender(sender);
        }
        
        message.setContent(content);
        // Chuẩn hóa attachment: để NULL nếu rỗng để không vi phạm CHECK constraint
        if (attachmentUrl == null || attachmentUrl.trim().isEmpty()) {
            message.setAttachmentUrl(null);
        } else {
            message.setAttachmentUrl(attachmentUrl);
        }
        message = messageRepository.save(message);

        // Nếu có ảnh đính kèm từ thư mục temp, di chuyển vào đúng cấu trúc lưu trữ và cập nhật URL
        if (message.getAttachmentUrl() != null && message.getAttachmentUrl().startsWith("/static/images/messages/temp/")) {
            try {
                String tempUrl = message.getAttachmentUrl();
                String filename = tempUrl.substring(tempUrl.lastIndexOf('/') + 1);
                String ext = "";
                int dot = filename.lastIndexOf('.');
                if (dot >= 0 && dot < filename.length() - 1) {
                    ext = filename.substring(dot);
                }

                String convFolder = "conversation-" + conversation.getId();
                String finalName = String.format("00_message-%d%s", message.getId(), ext);

                Path srcTemp = Paths.get("src/main/resources/static/images/messages/temp/", filename);
                Path targetTemp = Paths.get("target/classes/static/images/messages/temp/", filename);
                Path srcDestDir = Paths.get("src/main/resources/static/images/messages/", convFolder);
                Path targetDestDir = Paths.get("target/classes/static/images/messages/", convFolder);
                Files.createDirectories(srcDestDir);
                Files.createDirectories(targetDestDir);

                Path srcDest = srcDestDir.resolve(finalName);
                Path targetDest = targetDestDir.resolve(finalName);

                if (Files.exists(srcTemp)) {
                    Files.move(srcTemp, srcDest, StandardCopyOption.REPLACE_EXISTING);
                }
                if (Files.exists(targetTemp)) {
                    Files.move(targetTemp, targetDest, StandardCopyOption.REPLACE_EXISTING);
                }

                message.setAttachmentUrl("/static/images/messages/" + convFolder + "/" + finalName);
                message = messageRepository.save(message);
            } catch (Exception ignored) {}
        }

        return new ChatMessageDTO(message);
    }

    /**
     * Lấy danh sách tin nhắn của conversation
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessagesByConversationId(Integer conversationId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return messages.stream()
            .map(ChatMessageDTO::new)
            .collect(Collectors.toList());
    }

    /**
     * Lấy conversation theo ID
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getConversationById(Integer conversationId) {
        return conversationRepository.findById(conversationId)
            .map(ConversationDTO::new);
    }

    /**
     * Lấy conversation đang mở theo email guest (không tạo mới)
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getOpenConversationByGuestEmail(String guestEmail) {
        if (guestEmail == null || guestEmail.isBlank()) return Optional.empty();
        List<Conversation> list = conversationRepository
            .findAllByGuestEmailAndStatusOrderByCreatedAtDesc(guestEmail, Conversation.ConversationStatus.OPEN);
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(new ConversationDTO(list.get(0)));
    }

    /**
     * Lấy conversation đang mở theo userId (customer)
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getOpenConversationByUserId(Integer userId) {
        if (userId == null) return Optional.empty();
        return userRepository.findById(userId)
                .map(user -> {
                    List<Conversation> list = conversationRepository
                        .findAllByUserAndStatusOrderByCreatedAtDesc(user, Conversation.ConversationStatus.OPEN);
                    return list.isEmpty() ? null : new ConversationDTO(list.get(0));
                });
    }

    /**
     * Lấy danh sách conversation đang mở (cho Manager)
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> getOpenConversations() {
        List<Conversation> conversations = conversationRepository
            .findByStatusOrderByCreatedAtDesc(Conversation.ConversationStatus.OPEN);
        return conversations.stream()
            .map(this::convertToDTOWithStats)
            .collect(Collectors.toList());
    }

    /**
     * Lấy conversation của customer (hoặc guest theo email)
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> getOrCreateConversationForUser(Integer userId, String guestEmail) {
        Conversation conversation;
        
        if (userId != null) {
            // Customer: tìm conversation đang mở của user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            List<Conversation> existingList2 = conversationRepository
                .findAllByUserAndStatusOrderByCreatedAtDesc(user, Conversation.ConversationStatus.OPEN);
            if (!existingList2.isEmpty()) {
                return Optional.of(new ConversationDTO(existingList2.get(0)));
            }
            
            // Tạo mới
            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setStatus(Conversation.ConversationStatus.OPEN);
            conversation = conversationRepository.save(conversation);
        } else {
            // Guest: tìm conversation đang mở theo email
            List<Conversation> existingList = conversationRepository
                .findAllByGuestEmailAndStatusOrderByCreatedAtDesc(guestEmail, Conversation.ConversationStatus.OPEN);
            if (!existingList.isEmpty()) {
                return Optional.of(new ConversationDTO(existingList.get(0)));
            }
            // Tạo mới cần guestName + guestEmail (để luồng REST này tạo) → báo thiếu thông tin
            throw new RuntimeException("Cần guestName để tạo conversation mới cho guest");
        }
        
        return Optional.of(new ConversationDTO(conversation));
    }

    /**
     * Đóng conversation
     */
    @Transactional
    public void closeConversation(Integer conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation không tồn tại"));
        conversation.setStatus(Conversation.ConversationStatus.CLOSED);
        conversation.setClosedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * Thông báo cho tất cả Manager khi có tin nhắn mới
     * Có cơ chế chống spam: 1 thông báo/manager/conversation/5 phút
     */
    @Transactional
    public void notifyManagersNewMessage(Integer conversationId) {
        try {
            // Lấy tất cả Manager
            List<User> managers = userRepository.findByRoleName("MANAGER");
            
            // Lấy conversation để lấy thông tin
            Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation không tồn tại"));

            for (User manager : managers) {
                // Kiểm tra spam: xem có thông báo nào trong 5 phút gần đây không
                LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
                
                boolean hasRecentNotification = userNotificationRepository
                    .findByUserIdAndTitleContainingAndCreatedAtAfter(
                        manager.getId(),
                        "%Tin nhắn mới%",
                        fiveMinutesAgo
                    )
                    .stream()
                    .anyMatch(n -> n.getMessage() != null && 
                        n.getMessage().contains("cuộc hội thoại #" + conversationId));

                if (!hasRecentNotification) {
                    // Tạo thông báo
                    mocviet.entity.UserNotification notification = new mocviet.entity.UserNotification();
                    notification.setUser(manager);
                    notification.setTitle("Tin nhắn mới");
                    
                    String senderInfo;
                    if (conversation.getUser() != null) {
                        senderInfo = conversation.getUser().getFullName() != null 
                            ? conversation.getUser().getFullName() 
                            : conversation.getUser().getUsername();
                    } else {
                        senderInfo = conversation.getGuestName() != null 
                            ? conversation.getGuestName() 
                            : "Khách hàng";
                    }
                    
                    notification.setMessage(
                        String.format("Bạn có tin nhắn mới từ %s trong cuộc hội thoại #%d",
                            senderInfo, conversationId)
                    );
                    notification.setIsRead(false);
                    userNotificationRepository.save(notification);
                    
                    log.info("Đã tạo thông báo cho Manager {} về conversation {}", 
                        manager.getUsername(), conversationId);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi thông báo cho Manager về tin nhắn mới", e);
        }
    }

    /**
     * Convert Conversation entity sang DTO với thống kê
     */
    private ConversationDTO convertToDTOWithStats(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO(conversation);
        
        // Chưa có cơ chế đánh dấu đã đọc theo từng manager → tránh hiển thị sai
        // Đặt unreadCount = 0 để không gây hiểu nhầm là "chưa đọc"
        List<Message> messages = messageRepository
            .findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        dto.setUnreadCount(0L);
        
        // Lấy tin nhắn cuối cùng
        if (!messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessage(new ChatMessageDTO(lastMessage));
        }
        
        return dto;
    }
}
