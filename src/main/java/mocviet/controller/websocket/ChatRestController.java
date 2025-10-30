package mocviet.controller.websocket;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ConversationDTO;
import mocviet.dto.ChatMessageDTO;
import mocviet.service.chat.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    /**
     * Lấy conversation đang mở theo email guest (không tạo mới)
     */
    @GetMapping("/conversation/open")
    public ResponseEntity<?> getOpenConversationByGuestEmail(@RequestParam("guestEmail") String guestEmail) {
        Optional<ConversationDTO> conversationOpt = chatService.getOpenConversationByGuestEmail(guestEmail);
        return conversationOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lấy tất cả messages theo conversationId (public cho guest)
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@RequestParam("conversationId") Integer conversationId) {
        return ResponseEntity.ok(chatService.getMessagesByConversationId(conversationId));
    }

    /**
     * Lấy conversation đang mở theo userId (customer)
     */
    @GetMapping("/conversation/customer-open")
    public ResponseEntity<?> getOpenConversationByUser(@RequestParam("userId") Integer userId) {
        Optional<ConversationDTO> conversationOpt = chatService.getOpenConversationByUserId(userId);
        return conversationOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lấy thông tin conversation theo ID (OPEN/CLOSED) để client kiểm tra trạng thái trước khi gửi
     */
    @GetMapping("/conversation/info")
    public ResponseEntity<?> getConversationInfo(@RequestParam("conversationId") Integer conversationId) {
        Optional<ConversationDTO> conversationOpt = chatService.getConversationById(conversationId);
        return conversationOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


