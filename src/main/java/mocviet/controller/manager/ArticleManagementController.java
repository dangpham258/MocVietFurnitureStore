package mocviet.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.Article;
import mocviet.service.manager.ArticleImageService;
import mocviet.service.manager.IArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/manager/articles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ArticleManagementController {
    
    private final IArticleService articleService;
    private final ArticleImageService articleImageService;
    
    // ===== ARTICLE LIST (Danh sách bài viết đã đăng) =====
    
    @GetMapping
    @Transactional(readOnly = true)
    public String articleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String articleType,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String keyword,
            Authentication authentication,
            Model model) {
        
        String authorUsername = authentication.getName();
        
        // Reset page về 0 khi có keyword mới
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = 0;
        }
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Lấy danh sách bài viết với bộ lọc
        Page<ArticleListDTO> articles = articleService.getArticlesByAuthor(
                authorUsername, articleType, status, keyword, pageable);
        
        // Lấy thống kê dashboard
        ArticleDashboardDTO dashboard = articleService.getDashboard(authorUsername);
        
        model.addAttribute("articles", articles);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("articleType", articleType);
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Quản lý tin tức");
        model.addAttribute("activeMenu", "articles");
        
        return "manager/articles/article_list";
    }
    
    // ===== CREATE ARTICLE (Tạo bài viết mới) =====
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createArticleRequest", new CreateArticleRequest());
        model.addAttribute("products", articleService.getActiveProducts());
        model.addAttribute("pageTitle", "Tạo bài viết mới");
        model.addAttribute("activeMenu", "articles");
        
        return "manager/articles/article_create";
    }
    
    @PostMapping("/create")
    public String createArticle(
            @Valid @ModelAttribute("createArticleRequest") CreateArticleRequest request,
            BindingResult result,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(required = false) List<MultipartFile> contentFiles,
            @RequestParam(required = false) List<String> captions,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("products", articleService.getActiveProducts());
            model.addAttribute("pageTitle", "Tạo bài viết mới");
            model.addAttribute("activeMenu", "articles");
            return "manager/articles/article_create";
        }
        
        try {
            String authorUsername = authentication.getName();
            
            // Tạo bài viết
            Article article = articleService.createArticle(request, authorUsername);
            
            // Upload thumbnail (bắt buộc)
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                articleImageService.uploadThumbnail(article.getId(), thumbnailFile);
            } else {
                throw new IllegalArgumentException("Ảnh thumbnail là bắt buộc");
            }
            
            // Upload ảnh nội dung (tùy chọn)
            if (contentFiles != null && !contentFiles.isEmpty()) {
                // Filter out empty files
                List<MultipartFile> validFiles = contentFiles.stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .toList();
                
                if (!validFiles.isEmpty()) {
                    articleImageService.uploadContentImages(article.getId(), validFiles, captions);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Tạo bài viết thành công! " + (request.getStatus() ? "Đã xuất bản." : "Đang ở chế độ nháp."));
            return "redirect:/manager/articles";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            model.addAttribute("products", articleService.getActiveProducts());
            model.addAttribute("pageTitle", "Tạo bài viết mới");
            model.addAttribute("activeMenu", "articles");
            return "manager/articles/article_create";
        }
    }
    
    // ===== VIEW ARTICLE DETAIL =====
    
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String viewArticleDetail(
            @PathVariable Integer id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            String authorUsername = authentication.getName();
            ArticleDetailDTO article = articleService.getArticleDetail(id, authorUsername);
            
            model.addAttribute("article", article);
            model.addAttribute("pageTitle", "Chi tiết bài viết");
            model.addAttribute("activeMenu", "articles");
            
            return "manager/articles/article_detail";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/manager/articles";
        }
    }
    
    // ===== EDIT ARTICLE (Chỉnh sửa bài viết) =====
    
    @GetMapping("/{id}/edit")
    @Transactional(readOnly = true)
    public String showEditForm(
            @PathVariable Integer id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            String authorUsername = authentication.getName();
            ArticleDetailDTO article = articleService.getArticleDetail(id, authorUsername);
            
            // Convert to UpdateArticleRequest
            UpdateArticleRequest updateRequest = new UpdateArticleRequest();
            updateRequest.setId(article.getId());
            updateRequest.setTitle(article.getTitle());
            updateRequest.setArticleType(article.getArticleType());
            updateRequest.setSummary(article.getSummary());
            updateRequest.setContent(article.getContent());
            updateRequest.setLinkedProductId(article.getLinkedProductId());
            updateRequest.setIsFeatured(article.getIsFeatured());
            updateRequest.setStatus(article.getStatus());
            
            model.addAttribute("updateArticleRequest", updateRequest);
            model.addAttribute("article", article);
            model.addAttribute("products", articleService.getActiveProducts());
            model.addAttribute("pageTitle", "Chỉnh sửa bài viết");
            model.addAttribute("activeMenu", "articles");
            
            return "manager/articles/article_edit";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/manager/articles";
        }
    }
    
    @PostMapping("/{id}/edit")
    public String updateArticle(
            @PathVariable Integer id,
            @Valid @ModelAttribute("updateArticleRequest") UpdateArticleRequest request,
            BindingResult result,
            @RequestParam(required = false) MultipartFile thumbnailFile,
            @RequestParam(required = false) List<MultipartFile> contentFiles,
            @RequestParam(required = false) List<String> captions,
            @RequestParam(defaultValue = "false") boolean deleteOldImages,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            try {
                String authorUsername = authentication.getName();
                ArticleDetailDTO article = articleService.getArticleDetail(id, authorUsername);
                model.addAttribute("article", article);
                model.addAttribute("products", articleService.getActiveProducts());
                model.addAttribute("pageTitle", "Chỉnh sửa bài viết");
                model.addAttribute("activeMenu", "articles");
                return "manager/articles/article_edit";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
                return "redirect:/manager/articles";
            }
        }
        
        try {
            String authorUsername = authentication.getName();
            
            // Ensure id matches
            request.setId(id);
            
            // Cập nhật bài viết
            Article article = articleService.updateArticle(request, authorUsername);
            
            // Upload thumbnail mới (nếu có)
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                articleImageService.uploadThumbnail(article.getId(), thumbnailFile);
            }
            
            // Xử lý ảnh nội dung
            if (deleteOldImages) {
                // Xóa ảnh cũ trước khi upload ảnh mới
                articleImageService.deleteContentImages(article.getId());
            }
            
            // Upload ảnh nội dung mới (nếu có)
            if (contentFiles != null && !contentFiles.isEmpty()) {
                List<MultipartFile> validFiles = contentFiles.stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .toList();
                
                if (!validFiles.isEmpty()) {
                    articleImageService.uploadContentImages(article.getId(), validFiles, captions);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài viết thành công!");
            return "redirect:/manager/articles/" + id;
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            try {
                String authorUsername = authentication.getName();
                ArticleDetailDTO article = articleService.getArticleDetail(id, authorUsername);
                model.addAttribute("article", article);
                model.addAttribute("products", articleService.getActiveProducts());
                model.addAttribute("pageTitle", "Chỉnh sửa bài viết");
                model.addAttribute("activeMenu", "articles");
                return "manager/articles/article_edit";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + ex.getMessage());
                return "redirect:/manager/articles";
            }
        }
    }
}

