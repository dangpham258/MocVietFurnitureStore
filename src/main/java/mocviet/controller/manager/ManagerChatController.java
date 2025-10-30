package mocviet.controller.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.ConversationDTO;
import mocviet.dto.ChatMessageDTO;
import mocviet.service.chat.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller quản lý chat/conversation cho Manager
 * Route: /manager/messages/*
 */
@Controller
@RequestMapping("/manager/messages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerChatController {

    private final ChatService chatService;

    /**
     * Trang danh sách conversations đang mở
     */
    @GetMapping("")
    public String messagesList(Model model) {
        List<ConversationDTO> conversations = chatService.getOpenConversations();
        model.addAttribute("conversations", conversations);
        model.addAttribute("pageTitle", "Quản lý Tin nhắn");
        model.addAttribute("activeMenu", "messages");
        return "manager/messages/list";
    }

    /**
     * Trang chi tiết conversation và tin nhắn
     */
    @GetMapping("/{conversationId}")
    public String conversationDetail(
            @PathVariable Integer conversationId,
            Model model) {
        
        Optional<ConversationDTO> conversationOpt = chatService.getConversationById(conversationId);
        
        if (conversationOpt.isEmpty()) {
            model.addAttribute("error", "Cuộc hội thoại không tồn tại");
            return "redirect:/manager/messages";
        }
        
        ConversationDTO conversation = conversationOpt.get();
        List<ChatMessageDTO> messages = chatService.getMessagesByConversationId(conversationId);
        
        model.addAttribute("conversation", conversation);
        model.addAttribute("messages", messages);
        model.addAttribute("pageTitle", "Chi tiết Tin nhắn");
        model.addAttribute("activeMenu", "messages");
        
        return "manager/messages/detail";
    }

    /**
     * API: Đóng conversation
     */
    @PostMapping("/{conversationId}/close")
    @ResponseBody
    public ResponseEntity<?> closeConversation(@PathVariable Integer conversationId) {
        try {
            chatService.closeConversation(conversationId);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Đã đóng cuộc hội thoại\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API: Lấy danh sách conversations (AJAX)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<ConversationDTO>> getConversationsApi() {
        List<ConversationDTO> conversations = chatService.getOpenConversations();
        return ResponseEntity.ok(conversations);
    }

    /**
     * API: Lấy danh sách tin nhắn của conversation (AJAX)
     */
    @GetMapping("/api/{conversationId}/messages")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDTO>> getMessagesApi(
            @PathVariable Integer conversationId) {
        List<ChatMessageDTO> messages = chatService.getMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }
}
