package mocviet.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mocviet.dto.ChatMessageDTO;
import mocviet.dto.SendMessageDTO;
import mocviet.entity.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import mocviet.repository.UserRepository;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final mocviet.service.chat.ChatService chatService;
    private final UserRepository userRepository;

    /**
     * Xử lý tin nhắn từ client
     * Client gửi đến: /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    public void handleMessage(@Valid @Payload SendMessageDTO messageDTO, Authentication authentication) {
        try {
            log.info("Nhận tin nhắn từ WebSocket: conversationId={}, content={}", 
                messageDTO.getConversationId(), messageDTO.getContent());

            // Xác định sender
            Integer senderId = null;
            mocviet.entity.Message.SenderType senderType;

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                User entityUser = null;
                try {
                    if (principal instanceof User) {
                        entityUser = (User) principal;
                    } else if (principal instanceof UserDetails userDetails) {
                        // Tìm entity User theo username từ UserDetails
                        entityUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
                    }
                } catch (Exception ignored) {}

                if (entityUser != null) {
                    senderId = entityUser.getId();
                    String roleName = entityUser.getRole() != null ? entityUser.getRole().getName() : null;
                    if ("MANAGER".equals(roleName)) {
                        senderType = mocviet.entity.Message.SenderType.MANAGER;
                    } else {
                        senderType = mocviet.entity.Message.SenderType.CUSTOMER;
                    }
                } else {
                    // Không xác định được entity → thử dựa vào senderId client gửi lên (customer)
                    if (messageDTO.getSenderId() != null) {
                        try {
                            User fallbackUser = userRepository.findById(messageDTO.getSenderId()).orElse(null);
                            if (fallbackUser != null) {
                                senderId = fallbackUser.getId();
                                String roleName = fallbackUser.getRole() != null ? fallbackUser.getRole().getName() : null;
                                if ("MANAGER".equals(roleName)) {
                                    senderType = mocviet.entity.Message.SenderType.MANAGER;
                                } else {
                                    senderType = mocviet.entity.Message.SenderType.CUSTOMER;
                                }
                            } else {
                                senderType = mocviet.entity.Message.SenderType.GUEST;
                            }
                        } catch (Exception ex) {
                            senderType = mocviet.entity.Message.SenderType.GUEST;
                        }
                    } else {
                        senderType = mocviet.entity.Message.SenderType.GUEST;
                    }
                }
            } else {
                // Không có authentication → thử dựa trên senderId được client gửi lên
                if (messageDTO.getSenderId() != null) {
                    try {
                        User fallbackUser = userRepository.findById(messageDTO.getSenderId()).orElse(null);
                        if (fallbackUser != null) {
                            senderId = fallbackUser.getId();
                            String roleName = fallbackUser.getRole() != null ? fallbackUser.getRole().getName() : null;
                            if ("MANAGER".equals(roleName)) {
                                senderType = mocviet.entity.Message.SenderType.MANAGER;
                            } else {
                                senderType = mocviet.entity.Message.SenderType.CUSTOMER;
                            }
                        } else {
                            senderType = mocviet.entity.Message.SenderType.GUEST;
                        }
                    } catch (Exception ex) {
                        senderType = mocviet.entity.Message.SenderType.GUEST;
                    }
                } else {
                    senderType = mocviet.entity.Message.SenderType.GUEST;
                }
            }

            // Gọi service để lưu tin nhắn
            ChatMessageDTO savedMessage = chatService.saveMessage(
                messageDTO.getConversationId(),
                senderType,
                senderId,
                messageDTO.getGuestName() != null ? messageDTO.getGuestName() : "",
                messageDTO.getGuestEmail() != null ? messageDTO.getGuestEmail() : "",
                messageDTO.getContent(),
                // Để NULL thay vì chuỗi rỗng để không vi phạm CHECK constraint
                (messageDTO.getAttachmentUrl() != null && !messageDTO.getAttachmentUrl().isBlank())
                        ? messageDTO.getAttachmentUrl()
                        : null
            );

            // Gửi tin nhắn đến conversation channel
            String conversationTopic = "/topic/conversation/" + savedMessage.getConversationId();
            messagingTemplate.convertAndSend(conversationTopic, savedMessage);
            log.info("Đã gửi tin nhắn đến topic: {}", conversationTopic);

            // Kênh dự phòng: nếu là hội thoại của guest, phát thêm về kênh theo email để client guest (chưa có conversationId) nhận ngay
            try {
                chatService.getConversationById(savedMessage.getConversationId()).ifPresent(convDto -> {
                    String guestEmail = convDto.getGuestEmail();
                    if (guestEmail != null && !guestEmail.isBlank()) {
                        String guestTopic = "/topic/guest/" + guestEmail;
                        messagingTemplate.convertAndSend(guestTopic, savedMessage);
                        log.info("Đã gửi dự phòng đến topic guest: {}", guestTopic);
                    }
                });
            } catch (Exception ignored) {}

            // Thông báo realtime cho panel Manager để làm mới danh sách hội thoại ngay lập tức
            // (nhẹ, chỉ gửi event; phía Manager sẽ tự reload danh sách)
            try {
                messagingTemplate.convertAndSend("/topic/manager/conversations", savedMessage);
            } catch (Exception ignored) {
            }

            // Gửi thông báo đến Manager (nếu tin nhắn từ Guest/Customer)
            if (senderType == mocviet.entity.Message.SenderType.GUEST 
                || senderType == mocviet.entity.Message.SenderType.CUSTOMER) {
                chatService.notifyManagersNewMessage(savedMessage.getConversationId());
            }

        } catch (Exception e) {
            log.error("Lỗi khi xử lý tin nhắn WebSocket", e);
        }
    }
}
