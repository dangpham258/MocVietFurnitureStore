package mocviet.controller.guest;

import mocviet.entity.StaticPage; // Thêm import
import org.springframework.web.bind.annotation.PathVariable; // Thêm import
import java.util.Optional; // Thêm import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.ArticleSummaryDTO;
import mocviet.dto.GuestMessageRequestDTO;
import mocviet.dto.MessageResponse;
import mocviet.entity.Article;
import mocviet.service.guest.IGuestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // <<< XÓA HOẶC COMMENT DÒNG NÀY
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class GuestController {

    private final IGuestService guestService;

    // Phương thức xử lý trang Chính sách
    @GetMapping("/policy/{slug}")
    public String showPolicyPage(@PathVariable("slug") String slug, Model model) {
        Optional<StaticPage> staticPageOpt = guestService.getStaticPageBySlug(slug);

        if (staticPageOpt.isPresent()) {
            model.addAttribute("page", staticPageOpt.get());
            return "guest/policy"; // Trả về view policy.html
        } else {
            // Nếu không tìm thấy trang, chuyển hướng về trang chủ
            return "redirect:/";
        }
    }
    
    // Phương thức xử lý trang Tin tức
    @GetMapping("/news")
    public String showNewsPage(
            @RequestParam(name = "type", required = false) String typeParam,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 20;

        // <<<--- SỬA LỖI Ở ĐÂY: Bỏ phần Sort.by(...) đi --- >>>
        // Pageable pageable = PageRequest.of(page, pageSize, Sort.by("publishedAt").descending().and(Sort.by("id").ascending())); // DÒNG CŨ
        Pageable pageable = PageRequest.of(page, pageSize); // DÒNG MỚI (Không cần Sort)
        // <<<--- KẾT THÚC SỬA --- >>>

        Page<ArticleSummaryDTO> articlePage;
        Article.ArticleType selectedType = null;
        String typeFilterForUrl = null; // Biến để truyền vào link phân trang

        if (typeParam != null && !typeParam.isEmpty()) {
            try {
                selectedType = Article.ArticleType.valueOf(typeParam.toUpperCase());
                articlePage = guestService.findArticlesByType(selectedType, pageable);
                typeFilterForUrl = selectedType.name(); // Giữ lại type cho link phân trang
            } catch (IllegalArgumentException e) {
                articlePage = guestService.findAllArticles(pageable);
            }
        } else {
            articlePage = guestService.findAllArticles(pageable);
        }

        model.addAttribute("articlePage", articlePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("selectedType", selectedType != null ? selectedType.name() : "ALL");
        model.addAttribute("articleTypes", Article.ArticleType.values());
        model.addAttribute("typeFilter", typeFilterForUrl); // Truyền type lọc cho phân trang

        return "guest/news-list";
    }

    // Phương thức hiển thị trang Liên hệ
    @GetMapping("/contact")
    public String showContactPage(Model model) {
        if (!model.containsAttribute("guestMessageRequest")) {
             model.addAttribute("guestMessageRequest", new GuestMessageRequestDTO());
        }
        return "guest/contact";
    }

    // Phương thức xử lý gửi tin nhắn Liên hệ
    @PostMapping("/contact/send-message")
    public String handleSendMessage(
            @Valid @ModelAttribute("guestMessageRequest") GuestMessageRequestDTO messageRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "guest/contact";
        }

        MessageResponse response = guestService.handleGuestMessage(messageRequest);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
             return "redirect:/contact";
        } else {
            model.addAttribute("errorMessage", response.getMessage());
            messageRequest.setContent(""); // Xóa content
            return "guest/contact";
        }
    }

    // Phương thức xử lý trang Giới thiệu
    @GetMapping("/about")
    public String showAboutPage() {
        // Cần tạo file /templates/guest/about.html
        return "guest/about";
    }
}